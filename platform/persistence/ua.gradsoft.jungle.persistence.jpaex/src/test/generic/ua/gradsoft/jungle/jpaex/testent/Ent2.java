package ua.gradsoft.jungle.jpaex.testent;

import java.io.Serializable;
import javax.persistence.Basic;
import javax.persistence.Entity;
import javax.persistence.Id;

/**
 *
 * @author rssh
 */
@Entity
public class Ent2 implements Serializable
{

  @Id
  public int getX()
  { return x_; }

  public void setX(int x)
  {
    x_=x;
  }

  @Basic
  public String getName()
  { return name_; }

  public void setName(String name)
  { name_=name; }

  private int x_;
  private String name_;
}
