<?php
 require_once('generated.php');
 $x = new E1();  
 //var_dump($x);
 if (!$x->truth) {
   $x->truth = true;
 }
 //var_dump($x);
 if ($x->truth) {
   echo 'OK';
 } else {
   echo 'ERR: !$x->truth';
 }
?>
