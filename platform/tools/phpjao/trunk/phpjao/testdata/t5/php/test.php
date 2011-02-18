<?php
 require_once('generated.php');
 
 $serialized='{  "javaClass": "ua.gradsoft.t5.T5Dao",
                 "id":1 ,  
                 "en": "ONE" }';


 $s1 = json_decode($serialized,true);

 $json_error = json_last_error();
 if ($json_error!=JSON_ERROR_NONE) {
    echo "json_error=$json_error\n";
    if ($json_error==JSON_ERROR_SYNTAX) {
      echo 'JSON_EROR_SYNTAX'."\n";
    }
 }


 $result=PHPJAO::fromJson($s1);


 if ($result->id==1) {
   echo 'OK';
 } else {
   echo 'ERROR';
 }
 echo "\n";

 if ($result->en=='ONE') {
   echo 'OK';
 } else {
   echo 'ERROR';
 }

?>
