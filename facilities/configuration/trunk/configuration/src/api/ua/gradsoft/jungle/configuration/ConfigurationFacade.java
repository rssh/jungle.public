package ua.gradsoft.jungle.configuration;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import ua.gradsoft.jungle.auth.server.Permission;

/**
 * Facade for configuration items.
 **/ 
public interface ConfigurationFacade
{

  @Permission(name="jungle.configuration.read")
  List<ConfigItem>  getConfigItems(ConfigItemSelector itemSelector);

  @Permission(name="jungle.configuration.read")
  Integer           getConfigItemsCount(ConfigItemSelector itemSelector);

  @Permission(name="jungle.configuration.write")
  BigDecimal registerConfigItem(ConfigItem item);

  @Permission(name="jungle.configuration.write")
  void unregisterConfigItem(BigDecimal id);

  @Permission(name="jungle.configuration.read")
  public <T extends Serializable> T getConfigItemValue(Class<T> type, ConfigItemSelector itemSelector);

  @Permission(name="jungle.configuration.read")
  public List<Serializable>  getConfigItemValues(ConfigItemSelector itemSelector);

  @Permission(name="jungle.configuration.read")
  public String getStringConfigItemValue(ConfigItemSelector selector);

  @Permission(name="jungle.configuration.read")
  public Integer getIntegerConfigItemValue(ConfigItemSelector selector);

  @Permission(name="jungle.configuration.read")
  public Boolean getBooleanConfigItemValue(ConfigItemSelector selector);

  @Permission(name="jungle.configuration.read")
  public BigDecimal getBigDecimalConfigItemValue(ConfigItemSelector selector);

  @Permission(name="jungle.configuration.read")
  public Double   getDoubleConfigItemValue(ConfigItemSelector selector);

  @Permission(name="jungle.configuration.write")
  public <T extends Serializable> void  setConfigItemValue(ConfigItemSelector itemSelector, T value);

  /**
   * set config items, passed as objects.
   **/
  @Permission(name="jungle.configuration.write")
  public void  setConfigItemValues(Map<BigDecimal,Serializable> objects);

  /**
   * set config items, passed as strings.
   **/
  @Permission(name="jungle.configuration.write")            
  public void  setConfigItemStringValues(Map<BigDecimal,String> objects);

}
