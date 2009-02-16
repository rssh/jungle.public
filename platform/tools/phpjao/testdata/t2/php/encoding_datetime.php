<?php
require_once('PHPJAO.php');

$jsonArray = array(
  'javaClass' => 'java.util.Date',
  time => 0  
);

$o = PHPJAO::fromJson($jsonArray);

if (get_class($o)=='DateTime') {
  echo "OK\n";
}else{
  echo "Error: must be converted to datetime \n";
}

if ($o->format('U')=='0') {
  echo 'OK';
}else{
  echo 'Error';
}
echo "\n";

$narr = json_encode(PHPJAO::toJson($o));

# echo "narr=${narr}\n";
# echo "class(narr)="+get_class($narr)+"\n";

if ($narr['time']!=null) {
  echo "OK\n";
} else {
  echo 'Error';
}

?>

