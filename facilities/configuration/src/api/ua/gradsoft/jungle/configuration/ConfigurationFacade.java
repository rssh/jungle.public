package ua.gradsoft.jungle.configuration;

/**
 * Facade for configuration
 **/ 
public class ApplicationsConfiguration
{

  List<ConfigurationItemDescription>  getConfigDescription(String appName);

  void registerConfigItemDescription(ConfigItemDescription description);

  void unregisterConfigItemDescription(ConfigItemDescription description);

  public <T> T getConfigItem(Class<T> type, ConfigItemSelector itemSelector); 

  public <T> void  setConfigItem(ConfigItemSelector itemSelector, T item);

  public List<Object>  getConfigItems(ConfigItemSelector itemSelector);

  public int           getNumberOfConfigItems(ConfigItemSelector itemSelector);

  public void  setConfigItems(Map<BigDecimal,Object> objects);

}
