<?php require_once('PHPJAO.php');
?>
<?php class E1PHPJAOClassDescription extends PHPJAOClassDescription{
public  function __construct(){
  parent::__construct();
  
  $this->javaClass = 'ua.gradsoft.t1.E1';
  
  $this->phpClass = 'E1';
  
  $this->typesOfFields = array('name' => 'java.lang.String', 'value' => 'java.lang.String', 'truth' => 'java.lang.Boolean');
  
  }
 
 public  function newInstance(){
  return new E1() ;
  
  }
 
 }
class E1 extends PHPJAOPOJOBase{
static  $phpjaoClassDescription;
 
 public  function getPHPJAOClassDescription(){
  return self::$phpjaoClassDescription ;
  
  }
 
 public  $name;
 
 public  $value;
 
 public  $truth;
 
 }
E1::$phpjaoClassDescription = new E1PHPJAOClassDescription();
PHPJAO::registerType('ua.gradsoft.t1.E1', E1::$phpjaoClassDescription);
?>
