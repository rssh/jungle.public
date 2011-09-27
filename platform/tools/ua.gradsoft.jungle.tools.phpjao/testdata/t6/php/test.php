<?php
 require_once('generated.php');
 
 $serialized='{  "javaClass": "ua.gradsoft.t6.MyException",
                 "message":"message" ,  
                 "trace":"trace" ,  
                 "additionalInfo": "additionalInfo" }';


 $s1 = json_decode($serialized,true);

 $result=PHPJAO::fromJson($s1);


 if ($result->additionalInfo=='additionalInfo') {
   echo 'OK';
 } else {
   echo 'ERROR';
 }

?>
