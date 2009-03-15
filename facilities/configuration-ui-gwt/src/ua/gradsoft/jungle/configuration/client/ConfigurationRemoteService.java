package ua.gradsoft.jungle.configuration.client;


import com.google.gwt.user.client.rpc.RemoteService;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import ua.gradsoft.jungle.configuration.ConfigItem;
import ua.gradsoft.jungle.configuration.ConfigItemSelector;


public interface ConfigurationRemoteService extends RemoteService
{

  public List<ConfigItem> getConfigItems(ConfigItemSelector selector);

  public ConfigItem  getConfigItemById(BigDecimal id);

  public void  setConfigItemStringValues(Map<BigDecimal, String> objects);

}
