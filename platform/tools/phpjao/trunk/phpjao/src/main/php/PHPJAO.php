<?php

abstract class PHPJAOException extends Exception 
{
};

class PHPJAOTransportException extends PHPJAOException 
{
};

class PHPJAORemoteException extends PHPJAOException 
{
};


class PHPJAO
{

  private static $mapped = array();
  private static $customJsonMapping = array();
  private static $requestsIdCount = 0;

  public static function registerType($javaName, $phpName)
  { self::$mapped[$javaName]=$phpName; }

  public static function findType($javaName)
  { return self::$mapped[$javaName]; }

  public static function registerCustomType($phpName,$phpHelperName)
  {
    self::$customJsonMapping[$phpName] = new ReflectionClass($phpHelperName);
  }

  private static function isPrimitivePhpType(String $typeName)
  {
    return $typeName=="string" || $typeName=="integer"
         || $typeName=="boolean" || typeName=="float" ; 
  }

  static function isPrimitivePhpObject($o)
  {
    return is_string($o) || is_int($o) || is_float($o) || is_bool($o) ;
  }

  static function isPhpList($o)
  {
    if (is_array($o)) {
      $i=0;
      foreach($o as $key => $value) {
        if ($key!=$i) {
          return false;
        } else {
          ++$i;
        } 
      }
      return true;
    } else {
      return false;
    }
  }

  static function isJavaCollection(String $javaClassName) {
     return $javaClassName=="java.util.Map" 
          || $javaClassName=="java.util.HashMap" ;
  }

  static function toJson($o,$classHint=null) 
  {
    $javaClass=null;
    $reflectionClass=null;
    if (self::isPrimitivePhpObject($o)) {
       return $o;
    }else if (is_object($o)) {
       $className = get_class($o);
       if (isset(self::$customJsonMapping[$className])) {
         $toJson=self::$customJsonMapping[$className]->getMethod('toJson');
         return $toJson->invoke(null,$o);
       }else{
         $reflectionClass = new ReflectionClass(get_class($o));
         $javaClass=$reflectionClass->getConstant('javaClass');
       }
    }else if (is_null($o)) {
       return null;
    }

    $retval = array();
    if ($javaClass != null) {
       $retval['javaClass'] = $javaClass;
    }
    foreach($o as $key => $value) {
       $memberClassHint=null;
       if ($reflectionClass!=null) {
         $memberClassHint=$reflectionClass->getConstant("${key}_TYPE");
       }
       if ($value!=null) {
          $retval[$key]=self::toJson($value,$memberClassHint);
       }
       //TODO: check type
    }
    return $retval;
  }

  static function fromJson($o, $classHint=null)
  {
    $retval = null;
    if (self::isPrimitivePhpObject($o) || is_null($o)) {
      $retval = $o;
    }else{
      $phpType = null;
      if (isset($o['javaClass'])) {
        $phpType=self::findType($o['javaClass']);
      }
      if ($phpType!=null) {
        if (isset(self::$customJsonMapping[$phpType])) {
           $helperReflection = self::$customJsonMapping[$phpType];
           $fromJson=$helperReflection->getMethod('fromJson');
           $retval=$fromJson->invoke(null,$o);
        }else{
           $retval = new $phpType;
           $reflectionClass = new ReflectionClass($phpType);
           foreach($o as $key => $value) {
              // skip special
              if ($key!="javaClass") {
                 $memberType=$reflectionClass->getConstant("${key}_TYPE");
                 $retval->$key=self::fromJson($value, $memberType);
              }
           }
        }
      }else{
        $retval = array();
        foreach($o as $key => $value) {
            $retval[$key] = self::fromJson($value) ;
        }
      }
    }
    return $retval;
  }

  static function callOperation($url, $objname, $method, $arguments)
  {
    $requestArguments = self::toJson($arguments,null);
    $requestMethod=null; 
    if ($objname!=null) {
      $requestMethod="$objname.$method";
    } else {
      $requestMethod=$method;
    }
    $request = array(
        "method" => $requestMethod,
        "params" => $requestArguments,
        "id" => ++ self::$requestsIdCount
                    );
    $ch=curl_init($url);
    curl_setopt($ch,CURLOPT_RETURNTRANSFER,true);
    curl_setopt($ch,CURLOPT_POST,true);
    curl_setopt($ch,CURLOPT_POSTFIELDS,json_encode($request));
    $encodedResult=curl_exec($ch);
    if (curl_errno($ch)) {
        throw new PHPJAOTransportException("curl_error:".curl_error($ch));
    }
    curl_close($ch);
    $result=self::fromJson(json_decode($encodedResult,true));
    if (version_compare(PHP_VERSION,'5.3.0') == 1) {
      //will be enabled in PHP-5.3.0
      switch(json_last_error()) {
         case JSON_ERROR_DEPTH:
              throw new PHPJAOTransportException("json_error: structur to big");
         case JSON_ERROR_CTRL_CHAR:
              throw new PHPJAOTransportException("json_error: unexpected control character in reply");
         case JSON_ERROR_SYNTAX:
             throw new PHPJAOTransportException("json_error: non-json reply");
         case JSON_ERROR_NONE:
            // all ok.
            break;
       default:
           throw new PHPJAOTransportException("json_error:".json_last_error()); 
      }
   } else {
      // let's explicit check some bad cases.
      if ($result==NULL) {
        if (strlen($encodedResult)>10) {
          //echo "possible error: ($encodedResult)";
           throw new PHPJAOTransportException("json_parsing error:".substr($encodedResult,0,255)); 
        }
      }
   }
    if (is_array($result)) {
      if (isset($result['id']))
      {
          $resultId = $result['id'];
          # TODO: check that ID the same.
      }
      if (isset($result['error']))
      {
          $error = $result['error'];
          $message = $error['msg'];
          $code = $error['code'];
          throw new PHPJAORemoteException($message, $code);
      }
      if (isset($result['result']))
      {
          $result = $result['result'];
      }
    }
    return $result;
  }

  public static function createRemoteProxy($uri,$name=null)
  {
    return new PHPJaoRemoteProxy($uri,$name);
  }


}


class PHPJaoRemoteProxy
{
 public function __construct($theUrls, $theObjName)
 { 
   $this->objname=$theObjName;
   if (is_array($theUrls)) {
     $this->urls=$theUrls;
     $this->currIndex=rand()%count($theUrls);
     $this->url=$theUrls[$this->currIndex];
   } else {
     $this->urls=array($theUrls);
     $this->currIndex=0;
     $this->url=$theUrls;
   }
 }

 public function  getUrl() { return $url; }

 public function __call($method, $args)
 {
   $i=0;
   while($i<count($this->urls)) {
     try {
       return PHPJAO::callOperation($this->url,$this->objname,$method,$args);
     } catch (PHPJAOTransportException $ex) {
       error_log($ex->getMessage());
       $lastEroror=$ex;
       ++$i;
       if ($i!=count($this->urls)) {
         $this->currIndex=($this->currIndex+1)%count($this->urls);
         $this->url=$this->urls[$this->currIndex];
         error_log("PHPJAO:switched to $this->url");
       }
     }
   }
   error_log("PHPJAO: fata: all url-s for remote cal of $objname failed");
 }
 

 private $url;
 private $objname;
 private $urls;
 private $currIndex;
}

class DateTimePHPJAOHelper
{
   public static function toJson($object)
   {
     return array('javaClass' => 'java.util.Date',
                  'time'=>$object->format('U')*1000
                 );
   }

   public static function fromJson($array)
   {
       $unixtimestamp=$array['time']/1000;
       // dirty hack - wait for DateTime implementation in next PHP versions
       $str = strftime ("%Y-%m-%d %H:%M:%S", $unixtimestamp) ; 
       $retval = new DateTime($str);
       //$retval = new DateTime();
       //$retval->setTimestamp($unixtimestamp);
       return $retval;
   }

}
PHPJAO::registerType('java.util.Date','DateTime');
PHPJAO::registerCustomType('DateTime','DateTimePHPJAOHelper');

class BigDecimal
{
 public function __construct($theStrvalue)
   { 
     $this->strvalue=$theStrvalue; 
   }

 public function getString()
   { return $this->strvalue; }

 public function __toString()
   { return $this->strvalue; }

 public $strvalue;
}

/**
 * BigDecimal mapped to string
 */
class BigDecimalPHPJAOHelper
{

  public static function toJson($object)
  {
    if (is_string($object)) {
       return array('javaClass' => 'java.math.BigDecimal',
                    'strvalue' => $object);
    }else if (is_object($object)) {
       if ($object instanceof BigDecimal) {
         return array('javaClass' => 'java.math.BigDecimal',
                      'strvalue' => $object->strvalue);
       }else{
         $ocl=get_class($object);
         throw new PHPJAOException("Invailid class for BigDecimal ${ocl}");
       }
    }else{
       throw new PHPJAOException("Invalid BigDecimal object");
    }
  }
  
  public static function fromJson($object)
  {
    $retval=null;
    if (is_string($object)) {
      $retval=new BigDecimal($object);
    }else if (is_int($object)) {
      $retval=new BigDecimal("${object}");
    }else if (is_float($object)) {
      $retval=new BigDecimal("${object}");
    }else if (is_array($object)) {
      $class=$object['javaClass'];
      $strvalue=$object['strvalue'];
      if ($strvalue==null) {
        echo "(";
        foreach($object as $key=>$value) {
          echo "${key}=>${value} ";
        }
        echo ")\n";
        throw new PHPJAOException("Invalid BigDecimal object (without strvalue field)");
      }else{
        $retval=new BigDecimal($strvalue);
      }
    }else if (is_object($object)) {
      if ($object instanceof BigDecimal) {
        $retval=$object;
      }else{
        throw new PHPJAOException("Invalid BigDecimal object:");
      }
    }else{
      throw new PHPJAOException("Invalid BigDecimal object");
    }
    return $retval;
  }

}
PHPJAO::registerType('java.math.BigDecimal','BigDecimal');
PHPJAO::registerCustomType('BigDecimal','BigDecimalPHPJAOHelper');

class JavaClass
{
 public function __construct($theName)
  { $this->name=$theName; }

 public $name;

 const javaClass = "java.lang.Class";
 const name_TYPE = "java.lang.String";
}
PHPJAO::registerType('java.lang.Class','JavaClass');

class ArrayList
{
  public $list;
  const javaClass = "java.util.ArrayList";
}

class ArrayListPHPJAOHelper
{
  public static function toJson($object)
  {
    if (is_array($object)) {
      $l = $object['list'];
      return PHPJAO::toJson($o);
    }else if (is_object($object)) {
      if ($object instanceof ArrayList) {
        return array( 'javaClass' => 'java.util.ArrayList',
                      'list' => array( toJson($object->list) ));
      }else{
        $type = gettype($object);
        throw new PHPJAOException("can't transform object $type to ArrayList");
      }
    }else if ($object==null) {
      return null;
    }else{
      throw new PHPJAOException("can't transform object to ArrayList");
    }
  }

  public static function fromJson($object)
  {
    # transform to ArrayList
    if (is_array($object)) {
      $l=$object['list'];
      return PHPJAO::fromJson($l);
    }else{
      throw new PHPJAOException("can't transform ArrayList from JSON");
    }
  }

}
PHPJAO::registerType('java.util.ArrayList','ArrayList');
PHPJAO::registerCustomType('ArrayList','ArrayListPHPJAOHelper');

?>
