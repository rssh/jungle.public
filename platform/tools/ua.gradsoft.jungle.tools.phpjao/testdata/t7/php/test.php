<?php
 require_once('generated.php');
 
 $x = new NAnswer();

 $x->published = true;

 if ($result->published) {
   echo 'OK';
 } else {
   echo 'ERROR';
 }

?>
