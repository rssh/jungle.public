package ua.gradsoft.jungle.appregistry;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * One record in appregistry 
 **/ 
@Entity
@Table(name="appregistry")
public class ApplicationRecord
{

  @Id
  public  String getName()
  { return name_; }

  public void setName(String name)
  { name_=name; }

  @Column
  public String getVersion()
  { return version_; }

  public void setVersion(String version)
  { version_=version; }

  @Column
  public String getDescription()
  { return description_; }

  public void setDescription(String description)
  { description_=description; }

  public ApplicationRecord(String name, String version, String description)
  { name_=name; version_=version; description_=description; }

  private String name_;
  private String version_;
  private String description_;
}
