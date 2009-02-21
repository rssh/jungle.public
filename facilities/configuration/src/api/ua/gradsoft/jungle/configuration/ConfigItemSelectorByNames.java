package ua.gradsoft.jungle.configuration;


public class ConfigItemSelectorByNames implements ConfigItemSelector
{

  public ConfigItemSelecorByNames(String appName, String itemName)
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
 
  private String appName_;
  private String itemName_;
}
