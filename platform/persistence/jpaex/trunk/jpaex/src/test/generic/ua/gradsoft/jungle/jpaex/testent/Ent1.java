package ua.gradsoft.jungle.jpaex.testent;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToOne;


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


  @ManyToOne
  public List<Ent2> getEnt2s()
  {
    return ent2s_;
  }

  public void setEnt2s(List<Ent2> names)
  {
    ent2s_=names;  
  }


  private BigDecimal id_;
  private String name_;
  private List<Ent2> ent2s_;
  private String value_;
  private int    ix_;

}
