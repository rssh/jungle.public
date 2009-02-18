package ua.gradsoft.jungle.configuration;

import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;


@Entity
@Table(name="app_configuration_items")
public class ConfigurationItem implements Serializable
{

  public ConfigurationItem(String name, String value, String description)
  {
    name_=name;
    value_=value;
    description_=description;
  }



  @Id
  @Column(name="cname",length=255)
  public String getName()
  { return name_; }

  public void  setName(String name)
  { name_=name; }

  @Column(name="cvalue",length=255)
  public String getValue()
  { return value_; }

  public void setValue(String value)
  {  
    value_=value;
  }

  @Column(name="description",length=255)
  public String getDescription()
  { return description_; }

  public void setDescription(String description)
  {  
    description_=description;
  }

  private String name_;
  private String value_;
  private String description_;


  private static final long serialVersionUID = 1;

}
