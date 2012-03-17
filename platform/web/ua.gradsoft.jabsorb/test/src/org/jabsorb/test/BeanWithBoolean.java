package org.jabsorb.test;

public class BeanWithBoolean
{

  Boolean getBxObj() { return bxObj; }
  void    setBxObj(Boolean v)
     { bxObj = v; }

  boolean getBxVal() { return bxVal; }
  void    setBxVal(boolean v)
    { bxVal = v; }

  Integer getIxObj() { return ixObj; }
  void    setIxObj(Integer v)
    { ixObj = v; }


  int     getIxVal() { return ixVal; }
  void    setIxVal(int v)
    { ixVal = v; }

  private Boolean bxObj;
  private boolean bxVal;

  private Integer ixObj;
  private int     ixVal;
}
