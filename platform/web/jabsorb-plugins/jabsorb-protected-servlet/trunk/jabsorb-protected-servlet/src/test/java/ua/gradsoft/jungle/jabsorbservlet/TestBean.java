package ua.gradsoft.jungle.jabsorbservlet;


/**
 * test bean for access to it throught access control.
 **/ 
public class TestBean
{

  @Permission("permission1")
  int getP1()
   { return 10; }

  @Permission("permission2")
  int getP2()
   { return 20; }

  @Permission("*")
  int getP3()
   { return 30; }

  int getP4()
   { return 40; }

}

