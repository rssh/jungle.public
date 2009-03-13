package ua.gradsoft.jungle.configuration;

import java.io.Serializable;

public class ConfigItemSelectorByNames extends ConfigItemSelector implements Serializable
{

  public ConfigItemSelectorByNames()
  {
    this(null,null);
  }

  public ConfigItemSelectorByNames(String appName, String itemName)
   { 
     appName_=appName;
     itemName_=itemName; 
   }

  public String getAppName()
   { return appName_; }

  public void  setAppName(String appName)
   { appName_=appName; }

  public String getItemName()
   { return itemName_; }

  public void setItemName(String itemName)
  { itemName_=itemName; }
 
  private String appName_;
  private String itemName_;
}
