package ua.gradsoft.jungle.jabsorbservlet;

import java.math.BigDecimal;
import java.util.Map;
import ua.gradsoft.jungle.auth.server.Permission;


/**
 * test bean for access to it throught access control.
 **/ 
public class TestBean implements ITest
{

  public int getP1()
   { return 10; }

  public int getP2()
   { return 20; }

  public int getP3()
   { return 30; }

  public int getP4()
   { return 40; }

  public int getP5(int x)
  { return x+5; }

  public int testMapCall(Map<String,String> arg)
  {
    return arg.size();
  }

  public BigDecimal testBigDecimalRet()
  {
   return new BigDecimal("10000000000000000111111");
  }


}

