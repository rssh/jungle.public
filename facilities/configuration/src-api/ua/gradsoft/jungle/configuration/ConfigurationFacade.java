package ua.gradsoft.jungle.configuration;


public interface ConfigurationFacade
{
  
  public  boolean isDefined(String name);

  public  String  getStringItem(String name);

  public  boolean getBooleanItem(String name);

}
