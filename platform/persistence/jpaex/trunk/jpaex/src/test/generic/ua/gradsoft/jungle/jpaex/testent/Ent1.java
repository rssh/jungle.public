package ua.gradsoft.jungle.jpaex.testent;

import java.io.Serializable;
import java.math.BigDecimal;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;


@Entity
public class Ent1 implements Serializable
{

  @Id
  public BigDecimal getId()
  { return id_; }

  public void setId(BigDecimal id)
  { id_=id; }

  @Basic
  public String getName()
  { return name_; }

  public void setName(String name)
  { name_=name; }
  
  @Column(name="V", length=255)
  public String getValue()
  { return value_; }

  public void setValue(String value)
  { value_=value; }

  @Basic
  public int getIx()
  { return ix_; }

  public void setIx(int ix)
  { ix_=ix; }


  private BigDecimal id_;
  private String name_;
  private String value_;
  private int    ix_;

}
