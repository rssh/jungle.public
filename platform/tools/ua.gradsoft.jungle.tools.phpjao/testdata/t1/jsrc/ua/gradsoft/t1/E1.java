package ua.gradsoft.e1;

import java.io.Serializable;
import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public class E1 implements Serializable
{

  @Id()
  public String getName()
   { return name_; }

  public void   setName(String name)
   { name_=name; }

  public String getValue()
   { return value_; }

  public void   setValue(String value)
   { value_=value; }

  public Boolean isTruth()
   { return truth_; }

  public void   setTruth(Boolean truth)
   { truth_=truth; }

  private String name_;
  private String value_;
  private Boolean truth_;
}


