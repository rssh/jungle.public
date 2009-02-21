package ua.gradsoft.jungle.configuration;

public class ConfigItemDescription
{

 public BigDecimal getId()
  { return id_; }

 public void   setId(BigDecimal id)
  { id_=id; }

 public String getAppName()
  { return appName_; }

 public void   setAppName()
  { return appName_; }

 public String getName()
  { return name_; }

 public void  setName()
  { name_=name; }

 public ConfigItemType getType()
  { return type_; }

 public void setType(ConfigItemType type)
  { type_=type; }

 private BigDecimal id_;
 private String name_; 
 private ConfigItemType type_;
 private String description_;
 private String appname_;
 private int    maxLen_;
 private String regexpr_;
}
