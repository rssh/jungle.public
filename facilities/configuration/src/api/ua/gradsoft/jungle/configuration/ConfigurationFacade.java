package ua.gradsoft.jungle.configuration;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * Facade for configuration items.
 **/ 
public interface ConfigurationFacade
{

  List<ConfigItem>  getConfigItems(String appName);

  void registerConfigItem(ConfigItem item);

  void unregisterConfigItem(BigDecimal id);

  public <T> T getConfigItemValue(Class<T> type, ConfigItemSelector itemSelector);

  public List<Object>  getConfigItemValues(ConfigItemSelector itemSelector);
 
  public String getStringConfigItemValue(ConfigItemSelector selector);
  
  public Integer getIntegerConfigItemValue(ConfigItemSelector selector);
  
  public Boolean getBooleanConfigItemValue(ConfigItemSelector selector);

  public BigDecimal getBigDecimalConfigItemValue(ConfigItemSelector selector);

  public Double   getDoubleConfigItemValue(ConfigItemSelector selector);

  public <T> void  setConfigItemValue(ConfigItemSelector itemSelector, T value);

  public void  setConfigItemValues(Map<BigDecimal,Object> objects);

}
