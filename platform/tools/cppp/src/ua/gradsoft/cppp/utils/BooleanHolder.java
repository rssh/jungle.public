package ua.gradsoft.cppp.utils;

public class BooleanHolder
{

  public BooleanHolder(boolean b)
   { v_=b; }

  public boolean  get()
    { return v_; }

  public void  set(boolean b)
    { v_ = b; }

  private boolean v_;

}
