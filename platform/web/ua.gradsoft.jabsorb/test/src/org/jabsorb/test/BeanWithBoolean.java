package org.jabsorb.test;

import java.io.Serializable;

public class BeanWithBoolean implements Serializable
{

  public Boolean getBxObj() { return bxObj; }
  public void    setBxObj(Boolean v)
     { bxObj = v; }

  public Boolean getBxObj2() { return bxObj2; }
  public void    setBxObj2(Boolean v)
     { bxObj2 = v; }

  public boolean getBxVal() { return bxVal; }
  public void    setBxVal(boolean v)
    { bxVal = v; }

  public Integer getIxObj() { return ixObj; }
  public void    setIxObj(Integer v)
    { ixObj = v; }


  public int     getIxVal() { return ixVal; }
  public void    setIxVal(int v)
    { ixVal = v; }

  private Boolean bxObj;
  private Boolean bxObj2;
  private boolean bxVal;

  private Integer ixObj;
  private int     ixVal;
}
