<?php

abstract class PHPJAOException extends Exception 
{

   //private $httpCode;
};

class PHPJAOTransportException extends PHPJAOException 
{
};

class PHPJAORemoteException extends PHPJAOException 
{

  public function __construct($message=null,$code=null,$remoteTrace=null)
  {
    parent::__construct($message,$code);
    $this->remoteTrace = $remoteTrace;
  }

  public function getRemoteTrace()
    { return $remoteTrace; }

    private $remoteTrace;

};


class PHPJAO
{

  private static $mapped = array();
  private static $customJsonMapping = array();
  private static $requestsIdCount = 0;

  public static function registerType($javaName, $phpName)
  { self::$mapped[$javaName]=$phpName; }

  public static function findType($javaName)
  { return @self::$mapped[$javaName]; }

  public static function registerCustomType($phpName,$phpHelperName)
  {
    self::$customJsonMapping[$phpName] = $phpHelperName;
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

  static function toJson(&$o,$classHint=null) 
  {
    $javaClass=null;
    $classDescription = null;
    if (is_null($o)) {
       return null;
    }else if (self::isPrimitivePhpObject($o)) {
       return $o;
    }else if (is_object($o)) {
       $phpClassName = get_class($o);
       if (isset(self::$customJsonMapping[$phpClassName])) {
         $marshaller=self::$customJsonMapping[$phpClassName];
         return $marshaller->toJson($o);
       }else{
         if ($o instanceof PHPJAOPOJOBase) {
           $classDescription = $o->getPHPJAOClassDescription();
           $javaClass=$classDescription->javaClass;
         } else {
           $javaClass=$classHint;
         }
       }
    }else{
       $javaClass=$classHint;
    }

    $retval = array();
    if ($javaClass != null) {
       $retval['javaClass'] = $javaClass;
    }
    foreach($o as $key => $value) {
       $memberClassHint=null;
       if (!is_null($classDescription)) {
         $memberClassHint=$classDescription->typesOfFields[$key];
       }
       if (!is_null($value)) {
          $retval[$key]=self::toJson($value,$memberClassHint);
       } 
       //TODO: check type
    }
    return $retval;
  }

  static function fromJson(&$o, $classHint=null)
  {
    $retval = null;
    if (is_null($o) || self::isPrimitivePhpObject($o)) {
      $retval = $o;
    }else{
      $classDescription = null;
      if (isset($o['javaClass'])) {
        $classDescription=self::findType($o['javaClass']);
      }
      if (!is_null($classDescription)) {
        if (isset(self::$customJsonMapping[$classDescription->phpClass])) {
           $helper = self::$customJsonMapping[$classDescription->phpClass];
           $retval=$helper->fromJson($o);
        }else{
           $retval = $classDescription->newInstance();
           foreach($o as $key => $value) {
              // skip special
              if ($key!="javaClass") {
                 $memberType=$classDescription->typesOfFields[$key];
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

  static function initCurlHandle($url)
  {
    $ch=curl_init($url);
    curl_setopt($ch,CURLOPT_RETURNTRANSFER,true);
    curl_setopt($ch,CURLOPT_POST,true);
    curl_setopt($ch,CURLOPT_COOKIEFILE,'/dev/null');
    return $ch;
  }

  static function closeCurlHandle($ch)
  {
    curl_close($ch);
  }

  static function callOperation($ch, $objname, $method, $arguments)
  {
    $requestArguments = self::toJson($arguments,null);
    $requestMethod=null; 
    if (!is_null($objname)) {
      $requestMethod="$objname.$method";
    } else {
      $requestMethod=$method;
    }
    $request = array(
        "method" => $requestMethod,
        "params" => $requestArguments,
        "id" => ++ self::$requestsIdCount
                    );
    curl_setopt($ch,CURLOPT_POSTFIELDS,json_encode($request));
    $encodedResult=curl_exec($ch);
    if (curl_errno($ch)) {
        throw new PHPJAOTransportException("curl_error:".curl_error($ch));
    }
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
          if (isset($error['msg'])) {
             $message = $error['msg'];
          } else {
             $message = null;
          }
          if (isset($error['code'])) {
             $code = $error['code'];
          } else {
             $code = 0;
          }
          if (isset($error['trace'])) {
             $trace = $error['trace'];
          }else{
             $trace = null;
          }
          throw new PHPJAORemoteException($message, $code, $trace);
      }
      if (isset($result['result']))
      {
          $result = $result['result'];
      }
    }
    return $result;
  }

  public static function createRemoteProxy($cnOrUri,$name=null)
  {
    return new PHPJaoRemoteProxy($cnOrUri,$name);
  }

  public static function createConnection($login,$password,$urls)
  {
    return new PHPJaoConnection($login,$password,$urls);
  }

  public static function createAnonimousConnection($urls)
  {
    return new PHPJaoConnection(null,null,$urls);
  }

}

class PHPJaoConnection
{
 public function __construct($theLogin, $thePassword, $theUrls)
 {
   $this->login=$theLogin;
   $this->password=$thePassword;
   if (is_array($theUrls)) {
     $this->urls=$theUrls;
   } else {
     $this->urls=array($theUrls);
   }
   $this->sessionTicket=null;
   $this->currentIndex=rand()%count($this->urls);
   $this->currentUrl=$this->urls[$this->currentIndex];
   $this->curlHandle=null;
 }

 public function __destruct()
 {
   if (!is_null($this->curlHandle)) {
      PHPJAO::closeCurlHandle($this->curlHandle);
   }
 }

 public function isAnonimous()
 {
   return is_null($this->login);
 } 

 public function isLoggedIn()
 {
   return !is_null($this->sessionTicket);
 }

 public function getUrls()
 { return $this->urls; }

 /**
  * get curl handle, open connection if needed.
  **/
 public function getCurlHandle()
 {
   if (is_null($this->curlHandle)) {
     $this->open();
   }
   return $this->curlHandle;
 }

 public function getLogin()
 { return $this->login; }

 public function createProxy(String $name)
 {
   return new PHPJaoRemoteProxy($this,$name);
 }

 public function isOpen()
 {
   return !is_null($this->curlHandle);
 }

 public function open()
 {
  $this->curlHandle=PHPJAO::initCurlHandle($this->currentUrl);
 }

 public function close()
 {
  if ($this->isOpen()) {
     PHPJAO::closeCurlHandle($this->curlHandle);
     $this->curlHandle=null;
     $this->sessionTicket=null;
  }
 }

 public function loginIfNeeded()
 {
   if (!$this->isAnonimous() && !$this->isLoggedIn()) {
       $this->login();
   }
 }

 public function login()
 {
   if (is_null($this->curlHandle)) {
     $this->curlHandle=PHPJAO::initCurlHandle($this->currentUrl);
   }
   $method='getOrRestoreSessionTicket';
   $params=array(
     'plain',
     array( 
       'javaClass' => 'java.util.TreeMap',
       'map' => array(
          'username' => $this->login,
          'password' => $this->password    
       )      
     )
   );
   $this->sessionTicket = PHPJAO::callOperation($this->curlHandle,
                                                    'auth',$method,$params);
 }

 public function logout()
 {
   if (!is_null($this->curlHandle)) {
     PHPJAO::callOperation($this->curlHandle,'auth','logout',array());
     $this->sessionTicket = null;
   }
 }

 function switchNextUrl()
 {
   close();
   $this->currentIndex=($this->currentIndex+1)%count($this->urls);
   $this->currentUrl=$this->urls[$this->currentIndex];
   error_log("PHPJAO:switched to $this->currentUrl");
 }

 private $login; 
 private $password; 
 private $urls;
 private $sessionTicket;
 private $curlHandle;
 private $currentUrl;
 private $currentIndex;
}

class PHPJaoRemoteProxy
{

 public function __construct($cnOrUrls, $theObjName)
 { 
   if ($cnOrUrls instanceof PHPJaoConnection) {
      $this->connection=$cnOrUrls;
   }else if (is_array($cnOrUrls)) {
      $this->connection=new PhpJaoConnection(null,null,$cnOrUrls);
   }else if (is_string($cnOrUrls)) {
      $this->connection=new PhpJaoConnection(null,null,$cnOrUrls);
   }else{
      throw new InvalidArgumentException("bad type of first argument");
   }
   $this->objname=$theObjName;
 }

 public function  getUrl() { return $url; }

 public function __call($method, $args)
 {
   $i=0;
   $count=count($this->connection->getUrls());
   while($i<$count) {
     try {
       $this->connection->loginIfNeeded();
       return PHPJAO::callOperation($this->connection->getCurlHandle(),
                                    $this->objname,$method,$args);
     } catch (PHPJAOTransportException $ex) {
       error_log($ex->getMessage());
       $lastError=$ex;
       ++$i;
       if ($i!=$count) {
         $this->connection->switchNextUrl();
       }
     }
   }
   error_log("PHPJAO: fata: all url-s for remote cal of $this->objname failed");
   throw $lastError;
 }
 

 private $objname;
 private $connection;
}

abstract class PHPJAOClassDescription
{
  public $javaClass;
  public $phpClass;
  public $typesOfFields;

  public function __construct()
  {  }
  
  public abstract function newInstance();
}

abstract class PHPJAOPOJOBase
{
 public abstract function getPHPJAOClassDescription();
}

interface PHPJAOCustomMarshaller
{
   public function toJson($object);
   public function fromJson($array);
}

class DateTimePHPJAOClassDescription extends PHPJAOClassDescription
{
 public function __construct()
 {
   $this->javaClass='java.util.Date';
   $this->phpClass='DateTime';
   $this->typesOfFields=array('timestamp' => 'java.lang.long');
 }
 public function newInstance()
 {
   return new DateTime();
 }
}

class DateTimePHPJAOHelper 
{
   public  function toJson($object)
   {
     return array('javaClass' => 'java.util.Date',
                  'time'=>$object->format('U')*1000
                 );
   }

   public function fromJson($array)
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

PHPJAO::registerType('java.util.Date', new DateTimePHPJAOClassDescription());
PHPJAO::registerCustomType('DateTime', new DateTimePHPJAOHelper());

class BigDecimalPHPJAOClassDescription extends PHPJAOClassDescription
{
  public function __construct()
  {
   $this->javaClass = 'java.match.BigDecimal';
   $this->phpClass = 'BigDecimal';
   $this->typesOfFileds=array('strvalue'=>'java.lang.String');
  }
  public function newInstance()
  {  return new BigDecimal(); }
}

class BigDecimal extends PHPJAOPOJOBase
{
 public function __construct($theStrvalue=null)
   { 
     $this->strvalue=$theStrvalue; 
   }

 public function getString()
   { return $this->strvalue; }

 public function __toString()
   { return $this->strvalue; }

 public $strvalue;

 public function getPhpjaoClassDescription()
 {
  return phpjaoClassDescription;
 }

 static $phpjaoClassDescription;
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
PHPJAO::registerType('java.math.BigDecimal',
                            new BigDecimalPHPJAOClassDescription());
PHPJAO::registerCustomType('BigDecimal',new BigDecimalPHPJAOHelper());

class JavaClassPHPJAOClassDescription extends PHPJAOClassDescription
{
  public function __construct()
  {
   $this->javaClass = 'java.lang.Class';
   $this->phpClass = 'JavaClass';
   $this->typesOfFileds=array('name'=>'java.lang.String');
  }
  public function newInstance()
  {
   return new JavaClass('JavaClass:unknown');
  }
}

class JavaClass extends PHPJAOPOJOBase
{
 public function __construct($theName)
  { $this->name=$theName; }

 public $name;

 public function getPhpjaoClassDescription()
 {
  return self::$phpjaoClassDescription;
 }

 static $phpjaoClassDescription;
}
JavaClass::$phpjaoClassDescription=new JavaClassPHPJAOClassDescription;
PHPJAO::registerType('java.lang.Class',javaClass::$phpjaoClassDescription);

class JavaListPHPJAOClassDescription extends PHPJAOClassDescription
{
  public function __construct()
  {
   $this->javaClass = 'java.util.List';
   $this->phpClass = 'JavaList';
  }
  public function newInstance()
  {
   return new JavaList();
  }
}

class JavaList
{
  public $list;

  public function getPhpjaoClassDescription()
  {
    return self::$phpjaoClassDescription;
  }
  static $phpjaoClassDescription;
}

class JavaArrayListPHPJAOClassDescription extends PHPJAOClassDescription
{
  public function __construct()
  {
   $this->javaClass = 'java.util.ArrayList';
   $this->phpClass = 'JavaArrayList';
  }
  public function newInstance()
  {
   return new JavaArrayList();
  }
}

class JavaArrayList extends JavaList
{
  public function getPhpjaoClassDescription()
  {
    return self::$phpjaoClassDescription;
  }
  static $phpjaoClassDescription;
}

class JavaLinkedListPHPJAOClassDescription extends PHPJAOClassDescription
{
  public function __construct()
  {
   $this->javaClass = 'java.util.LinkedList';
   $this->phpClass = 'JavaLinkedList';
  }
  public function newInstance()
  {
   return new JavaLinkedList();
  }
}


class JavaLinkedList extends JavaList
{
  public function getPhpjaoClassDescription()
  {
    return self::$phpjaoClassDescription;
  }
  static $phpjaoClassDescription;
}

class ListPHPJAOHelper
{
  public static function toJson($object)
  {
    if (is_array($object)) {
      $l = $object['list'];
      return PHPJAO::toJson($o);
    }else if (is_object($object)) {
      if ($object instanceof JavaArrayList) {
        return array( 'javaClass' => 'java.util.ArrayList',
                      'list' => array( toJson($object->list) ));
      } else if ($object instanceof JavaLinkedList) {
        return array( 'javaClass' => 'java.util.LinkedList',
                      'list' => array( toJson($object->list) ));
      } else if ($object instanceof JavaList) {
        return array( 'javaClass' => 'java.util.List',
                      'list' => array( toJson($object->list) ));
      }else{
        $type = gettype($object);
        throw new PHPJAOException("can't transform object $type to List");
      }
    }else if (is_null($object)) {
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

  static $instance;
}
JavaList::$phpjaoClassDescription=new JavaListPHPJAOClassDescription();
PHPJAO::registerType('java.util.List',JavaList::$phpjaoClassDescription);
JavaArrayList::$phpjaoClassDescription=
                                 new JavaArrayListPHPJAOClassDescription();
PHPJAO::registerType('java.util.ArrayList',
                                 JavaArrayList::$phpjaoClassDescription);
JavaLinkedList::$phpjaoClassDescription=
                                 new JavaLinkedListPHPJAOClassDescription();
PHPJAO::registerType('java.util.LinkedList','JavaLinkedList',
                                 JavaLinkedList::$phpjaoClassDescription);
ListPHPJAOHelper::$instance = new ListPHPJAOHelper();
PHPJAO::registerCustomType('JavaList',ListPHPJAOHelper::$instance);
PHPJAO::registerCustomType('JavaArrayList',ListPHPJAOHelper::$instance);
PHPJAO::registerCustomType('JavaLinkedList',ListPHPJAOHelper::$instance);


?>
