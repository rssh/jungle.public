package ua.gradsoft.jungle.jabsorbservlet;

import ua.gradsoft.jungle.auth.server.Permission;


/**
 * test bean for access to it throught access control.
 **/ 
public class TestBean implements ITest
{

  @Permission(name="p1")
  public int getP1()
   { return 10; }

  @Permission(name="p2")
  public int getP2()
   { return 20; }

  @Permission(name="*")
  public int getP3()
   { return 30; }

  public int getP4()
   { return 40; }

  public int getP5(int x)
  { return x+5; }

}

