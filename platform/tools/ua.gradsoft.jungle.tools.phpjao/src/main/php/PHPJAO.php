<?php

abstract class PHPJAOException extends Exception 
{

   //private $httpCode;
};

class PHPJAOTransportException extends PHPJAOException 
{
};

class PHPJAOMarshallingException extends PHPJAOException 
{
}


class PHPJAOInternalException extends PHPJAOException 
{
  public function __construct($message)
   { parent::__construct($message); }
}


class PHPJAORemoteException extends PHPJAOException 
{

  public function __construct($message=null,$code=null,$remoteTrace=null)
  {
    parent::__construct($message,$code);
    $this->remoteTrace = $remoteTrace;
  }

  public function getRemoteTrace()
    { return $this->remoteTrace; }

    private $remoteTrace;

};


class JavaException extends PHPJAORemoteException
{
   
  public function __construct($message=null, $code=null, $classname=null, $remoteTrace=null)
  {
    parent::__construct($message,$code,$remoteTrace);
    $this->javaClass = $classname;
  }

  private function getJavaClass()
   { return $this->javaClass; }

  private $javaClass;

  static public $marshallingHelper;
};


class JavaRuntimeException extends JavaException
{
  public function __construct($message=null, $code=null, $classname=null, $remoteTrace=null)
  {
    parent::__construct($message,$code,$remoteTrace);
  }
};


class PHPJAO
{

  private static $mapped = array();
  private static $customJsonMapping = array();
  private static $requestsIdCount = 0;
  private static $connectionTimeout = 0;

  private static $defaultTimezoneIsSet = false;

  public static function registerType($javaName, $phpName)
  { self::$mapped[$javaName]=$phpName; }

  public static function findType($javaName)
  { return @self::$mapped[$javaName]; }

  public static function registerCustomType($phpName,$phpHelperName)
  {
    self::$customJsonMapping[$phpName] = $phpHelperName;
  }

  public static function setConnectionTimeout($connectionTimeout){
      self::$connectionTimeout = $connectionTimeout;
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
         $memberClassHint=@$classDescription->typesOfFields[$key];
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
        $helper = @self::$customJsonMapping[$classDescription->phpClass];
        if (!is_null($helper)) {
           $retval=$helper->fromJson($o);
        }else{
           $retval = $classDescription->newInstance();
           foreach($o as $key => $value) {
              // skip special
              if ($key!='javaClass') {
                 $memberType=@$classDescription->typesOfFields[$key];
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
    if (self::$connectionTimeout != 0)
        curl_setopt($ch, CURLOPT_CONNECTTIMEOUT, self::$connectionTimeout);
    curl_setopt($ch, CURLOPT_RETURNTRANSFER, true);
    curl_setopt($ch, CURLOPT_POST, true);
    curl_setopt($ch, CURLOPT_COOKIEFILE, '/dev/null');
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
        throw new PHPJAOTransportException("curl_error_number:" . curl_errno($ch) . " curl_error:".curl_error($ch));
    }
    $decodedResult = json_decode($encodedResult,true);
    $result=self::fromJson($decodedResult);
    if (version_compare(PHP_VERSION,'5.3.0') >= 0) {
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
      if (!isset($result['result']) && !isset($result['error'])){
          $result = null;
      }	
      if (isset($result['error']))
      {
          $error = $result['error'];
          if (is_object($error)) {
            if ($error instanceof Exception) {
               throw $error;
            } else {
               // object but not exception - impossible.
               throw new PHPJAORemoteException(""+$error);
            }
          } else {
            if (isset($error['msg'])) {
             $message = $error['msg'];
            } else if (isset($error['message'])) {
             $message = $error['message'];
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
            $javaClass=null;
            if (isset($error['javaClass'])) {
             $javaClass=$error['javaClass'];
             // check - if this class is mapped to php
             $classDescription = self::findType($javaClass);  
             if ($classDescription==null) {
                 // java exception without php mapping.
                 throw new JavaException($message, $code, $javaClass, $trace);
             } else {
                 //
                 throw self::fromJson($error,$javaClass);
             }
            } else {
             // look's like this exception is not coming throeught our exception transformer.
             throw new PHPJAORemoteException($message, $code, $trace);
            }
          }
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

  public static function checkDefaultTimezone()
  {
    if (!self::$defaultTimezoneIsSet) {
      self::$defaultTimezoneIsSet=date_default_timezone_set(
                                           date_default_timezone_get());
    }
  }

}

class PHPJaoConnection
{
 public function __construct($theLogin, $thePassword, $theUrls, $md5_auth = null)
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
   $this->usemd5 = $md5_auth;
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

 public function getCurrentUrl(){
    return $this->currentUrl;
 }

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

 public function checkUserPermission($permission){
  if (is_null($this->curlHandle)) {
     $this->curlHandle=PHPJAO::initCurlHandle($this->currentUrl);
   }
   $method='checkUserPermission';
   $params = array(
       $this->sessionTicket,
       $permission,
       array('javaClass' => 'java.util.TreeMap',
       'map' => array('a'=>'1'))
   );  
   
       return PHPJAO::callOperation($this->curlHandle,
                                                    'auth',$method,$params);
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
   if ($this->usemd5){
    $params[1]['map']['crypt_pair'] = $this->usemd5;
   }

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
   $this->close();
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
 private $usemd5;
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

 public function  getUrls() { return $this->connection->getUrls(); }

 public function  getCurrentUrl() { return $this->connection->getCurrentUrl(); }

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
   error_log("PHPJAO: fatal: all url-s for remote cal of $this->objname failed");
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


class JavaExceptionPHPJAOClassDescription extends PHPJAOClassDescription
{
 public function __construct()
 {
  $this->javaClass='java.lang.Exception';
  $this->phpClass='JavaException';

  // exception fields are passed via constructor, becouse approprative fields are private.
  // note, that we pass also full stacktrace and cause, but since it is not used in php,
  // we does not unmarshall them.
  $this->typesOfFields=array();
 }

  public function newInstance()
  { throw new PHPJAOInternalException("Internal error: this method must not be called"); }

}

class JavaRuntimeExceptionPHPJaoClassDescription extends JavaExceptionPHPJAOClassDescription
{
  public function __construct() 
  {
   parent::__construct();
   $this->javaClass = 'java.lang.RuntimeException';
  }

}

class JavaExceptionPHPJAOHelper 
{
   public function toJson($object)
   {
     $retval=array('msg'=>$object->getMessage(),
                   'code'=>$object->getCode(),
                   'javaClass'=>$object->javaClass,
                   'trace' => $object.getRemoteTrace());
     $classDescription = PHPJAO::findType($javaClass); 
     if ($classDescription!=null) {
       foreach($classDescription->typesOfFields as $key => $ftype) {
          $retval[$key]=$object->$key;
       }
     }
     return $retval;
   }

   public function fromJson($object) 
   {
     $message = $object['msg'];
     $code = $object['code'];
     $trace = $object['trace'];
     $javaClass = $object['javaClass'];
     $classDescription = PHPJAO::findType($javaClass); 
     $retval = null;
     if ($classDescription!=null) {
        $phpClass = $classDescription->phpClass;
        $retval = new $phpClass($message,$code,$javaClass,$trace);
        foreach($object as $key => $value) {
          if ($key!='message' && $key!='stackTrace' && $key!='trace' && $key!='code'
            && $key!='javaClass' ) {
           $memberType=@$classDescription->typesOfFields[$key];
           $retval->$key=PHPJAO::fromJson($value, $memberType);
          }
        }
     } else {
        $retval = new JavaException($message,$code,$javaClass,$trace);
     }
     return $retval;
   }

}
JavaException::$marshallingHelper = new JavaExceptionPHPJAOHelper();

class DateTimePHPJAOClassDescription extends PHPJAOClassDescription
{
 public function __construct()
 {
   $this->javaClass='java.util.Date';
   $this->phpClass='DateTime';
   $this->typesOfFields=array('time' => 'java.lang.long');
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
       if (version_compare(PHP_VERSION,'5.3.0')>=0) {
         PHPJAO::checkDefaultTimezone();
         $retval = new DateTime();
         $retval->setTimestamp($unixtimestamp);
       } else {
         // dirty hack 
         $str = strftime ("%Y-%m-%d %H:%M:%S", $unixtimestamp) ; 
         $retval = new DateTime($str);
       }
       return $retval;
   }

}

PHPJAO::registerType('java.sql.Date', new DateTimePHPJAOClassDescription());
PHPJAO::registerType('java.sql.Timestamp', new DateTimePHPJAOClassDescription());
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
  return self::$phpjaoClassDescription;
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
         throw new PHPJAOInternalException("Invailid class for BigDecimal ${ocl}");
       }
    }else{
       throw new PHPJAOInternalException("Invalid BigDecimal object");
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
        throw new PHPJAOMarshallingException("Invalid BigDecimal object (without strvalue field)");
      }else{
        $retval=new BigDecimal($strvalue);
      }
    }else if (is_object($object)) {
      if ($object instanceof BigDecimal) {
        $retval=$object;
      }
    }else{
      throw new PHPJAOMarshallingException("Invalid BigDecimal object");
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

  public function __construct()
  {
   $this->list=func_get_args();
  }

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

  public function __construct()
  {
   $this->list=func_get_args();
  }

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

  public function __construct()
  {
   $this->list=func_get_args();
  }

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
      return PHPJAO::toJson($l);
    }else if (is_object($object)) {
      if ($object instanceof JavaArrayList) {
        return array( 'javaClass' => 'java.util.ArrayList',
                      'list' => PHPJAO::toJson($object->list) );
      } else if ($object instanceof JavaLinkedList) {
        return array( 'javaClass' => 'java.util.LinkedList',
                      'list' => PHPJAO::toJson($object->list) );
      } else if ($object instanceof JavaList) {
        return array( 'javaClass' => 'java.util.List',
                      'list' => PHPJAO::toJson($object->list) );
      }else{
        $type = gettype($object);
        throw new PHPJAOMarshallingException("can't transform object $type to List");
      }
    }else if (is_null($object)) {
      return null;
    }else{
      throw new PHPJAOMarshallingException("can't transform object to ArrayList");
    }
  }

  public static function fromJson($object)
  {
    # transform to ArrayList
    if (is_array($object)) {
      $l=$object['list'];
      return PHPJAO::fromJson($l);
    }else{
      throw new PHPJAOMarshallingException("can't transform ArrayList from JSON");
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
PHPJAO::registerType('java.util.LinkedList',
                                 JavaLinkedList::$phpjaoClassDescription);
ListPHPJAOHelper::$instance = new ListPHPJAOHelper();
PHPJAO::registerCustomType('JavaList',ListPHPJAOHelper::$instance);
PHPJAO::registerCustomType('JavaArrayList',ListPHPJAOHelper::$instance);
PHPJAO::registerCustomType('JavaLinkedList',ListPHPJAOHelper::$instance);

class JavaMapPHPJAOClassDescription extends PHPJAOClassDescription
{
  public function __construct()
  {
   $this->javaClass = 'java.util.Map';
   $this->phpClass = 'JavaMap';
  }

  public function newInstance()
  {
   return new JavaMap();
  }
}

class JavaMap
{

  public function __construct()
  {
   if (func_num_args()==1){
      $this->map=func_get_arg(0);
   }
  }

  public $map;

  public function getPhpjaoClassDescription()
  {
    return self::$phpjaoClassDescription;
  }
  static $phpjaoClassDescription;
}

class JavaTreeMapPHPJAOClassDescription extends PHPJAOClassDescription
{
  public function __construct()
  {
   $this->javaClass = 'java.util.TreeMap';
   $this->phpClass = 'JavaTreeMap';
  }
  public function newInstance()
  {
   return new JavaTreeMap();
  }
}

class JavaTreeMap extends JavaMap
{

  public function __construct()
  {
   if (func_num_args()==1){
      $this->map=func_get_arg(0);
   }
  }

  public function getPhpjaoClassDescription()
  {
    return self::$phpjaoClassDescription;
  }
  static $phpjaoClassDescription;
}

class MapPHPJAOHelper
{
  public static function toJson($object)
  {
    if (is_array($object)) {
      $l = $object['map'];
      return PHPJAO::toJson($l);
    }else if (is_object($object)) {
      if ($object instanceof JavaTreeMap) {
        return array( 'javaClass' => 'java.util.TreeMap',
                      'map' => PHPJAO::toJson($object->map) );
      } else if ($object instanceof JavaMap) {
        return array( 'javaClass' => 'java.util.Map',
                      'map' => PHPJAO::toJson($object->map) );
      }else{
        $type = gettype($object);
        throw new PHPJAOMarshallingException("can't transform object $type to Map");
      }
    }else if (is_null($object)) {
      return null;
    }else{
      throw new PHPJAOMarshallingException("can't transform object to TreeMap");
    }
  }

  /* 
   * TODO add test,
   */
  public static function fromJson($object)
  {
    # transform to TreeMap
   
    if (is_array($object)) {
      $l=$object['map'];
      return PHPJAO::fromJson($l);
    }else{
      throw new PHPJAOMarshallingException("can't transform TreeMap from JSON");
    } 
     
  }

  static $instance;
}
JavaMap::$phpjaoClassDescription=new JavaMapPHPJAOClassDescription();
PHPJAO::registerType('java.util.Map',JavaMap::$phpjaoClassDescription);
JavaTreeMap::$phpjaoClassDescription=
                                 new JavaTreeMapPHPJAOClassDescription();
PHPJAO::registerType('java.util.TreeMap',
                                 JavaTreeMap::$phpjaoClassDescription);

MapPHPJAOHelper::$instance = new MapPHPJAOHelper();
PHPJAO::registerCustomType('JavaMap',MapPHPJAOHelper::$instance);
PHPJAO::registerCustomType('JavaTreeMap',MapPHPJAOHelper::$instance);

?>
