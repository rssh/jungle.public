<?php
require_once('PHPJAO.php');

function testFun()
{
  $remote = PHPJAO::createRemoteProxy("http://127.0.0.1:8080/JSON-RPC",
                                      "testBean");
  $p1 = $remote->getP5(1);
  var_dump($p1);
}

testFun();

?>
