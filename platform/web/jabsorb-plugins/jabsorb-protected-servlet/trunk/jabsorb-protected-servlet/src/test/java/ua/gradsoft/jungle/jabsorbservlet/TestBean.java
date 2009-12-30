package ua.gradsoft.jungle.jabsorbservlet;

import ua.gradsoft.jungle.auth.server.Permission;


/**
 * test bean for access to it throught access control.
 **/ 
public class TestBean
{

  @Permission(name="permission1")
  int getP1()
   { return 10; }

  @Permission(name="permission2")
  int getP2()
   { return 20; }

  @Permission(name="*")
  int getP3()
   { return 30; }

  int getP4()
   { return 40; }

}

