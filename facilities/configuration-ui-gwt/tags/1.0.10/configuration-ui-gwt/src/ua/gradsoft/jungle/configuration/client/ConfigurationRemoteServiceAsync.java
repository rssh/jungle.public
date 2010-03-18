package ua.gradsoft.jungle.configuration.client;

import com.google.gwt.user.client.rpc.AsyncCallback;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import ua.gradsoft.jungle.configuration.ConfigItem;
import ua.gradsoft.jungle.configuration.ConfigItemSelector;

public interface ConfigurationRemoteServiceAsync
{

  public void getConfigItems(ConfigItemSelector selector,
                             AsyncCallback<List<ConfigItem>> callback);

  public void getConfigItemsCount(ConfigItemSelector selector,
                                  AsyncCallback<Integer> callback);

  public void  getConfigItemById(BigDecimal id, AsyncCallback<ConfigItem> callback);

  public void  setConfigItemStringValues(Map<BigDecimal,String> objects,
                                         AsyncCallback<Void> callback);

}
