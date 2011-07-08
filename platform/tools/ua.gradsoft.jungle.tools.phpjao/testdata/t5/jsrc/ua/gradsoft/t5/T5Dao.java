package ua.gradsoft.t5;

import java.io.Serializable;
//import javax.persistence.Entity;
//import javax.persistence.Enumerated;
//import javax.persistence.EnumType;

//@Entity
public class T5Dao implements Serializable
{

  //@Id
  public Integer getId()
  { return id; }

  public void setId(Integer id)
  { this.id=id; }

  //@Enumerated(EnumType.ORDINAL)
  public T5Enum getEn()
  { return en; }

  public void  setEn(T5Enum en)
  { this.en=en; }

  private Integer id;
  private T5Enum en;
}
