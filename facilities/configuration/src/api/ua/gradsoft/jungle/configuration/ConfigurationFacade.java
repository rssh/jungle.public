package ua.gradsoft.jungle.configuration;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * Facade for configuration items.
 **/ 
public interface ConfigurationFacade
{

  List<ConfigItem>  getConfigItems(ConfigItemSelector itemSelector);
 
  Integer           getConfigItemsCount(ConfigItemSelector itemSelector);

  BigDecimal registerConfigItem(ConfigItem item);

  void unregisterConfigItem(BigDecimal id);

  public <T extends Serializable> T getConfigItemValue(Class<T> type, ConfigItemSelector itemSelector);

  public List<Serializable>  getConfigItemValues(ConfigItemSelector itemSelector);
 
  public String getStringConfigItemValue(ConfigItemSelector selector);
  
  public Integer getIntegerConfigItemValue(ConfigItemSelector selector);
  
  public Boolean getBooleanConfigItemValue(ConfigItemSelector selector);

  public BigDecimal getBigDecimalConfigItemValue(ConfigItemSelector selector);

  public Double   getDoubleConfigItemValue(ConfigItemSelector selector);

  public <T extends Serializable> void  setConfigItemValue(ConfigItemSelector itemSelector, T value);

  public void  setConfigItemValues(Map<BigDecimal,Serializable> objects);

}
