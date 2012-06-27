<?php
 require_once('generated.php');
 
 $x = new NAnswer();

 $x->published = true;

 $typesOfFields = NAnswer::$phpjaoClassDescription->typesOfFields;

 if ($x->published && count($typesOfFields) > 0) {
   echo 'OK';
 } else {
   echo 'ERROR';
 }

?>
