package ua.gradsoft.t4;

import java.io.Serializable;

public class T4 implements Serializable
{

  int getOrderBy()
  { return orderBy_; }

  void setOrderBy(int orderBy)
  { orderBy_=orderBy; }

  private int orderBy_;

  public static final int MY_CONSTANT = 1;

}
