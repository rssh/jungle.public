package ua.gradsoft.jungle.configuration.server;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import ua.gradsoft.jungle.configuration.ConfigItem;
import ua.gradsoft.jungle.configuration.ConfigItemSelector;
import ua.gradsoft.jungle.configuration.ConfigItemType;
import ua.gradsoft.jungle.configuration.client.ConfigurationRemoteService;

public class TestConfigurationRemoteServiceImpl extends RemoteServiceServlet
                                          implements ConfigurationRemoteService

{

    public List<ConfigItem> getConfigItems(ConfigItemSelector selector) {
        init();
        List<ConfigItem> retval = new ArrayList<ConfigItem>();
        retval.addAll(storage_.values());
        System.err.println("returned "+retval.size()+" values");
        return retval;
    }

    public Integer getConfigItemsCount(ConfigItemSelector selector) {
        init();
        return storage_.size();
    }



    public ConfigItem getConfigItemById(BigDecimal id)
    {
       init(); 
       return storage_.get(id);
    }

    public void setConfigItemStringValues(Map<BigDecimal, String> objects) {
        init();
        for(Map.Entry<BigDecimal,String> e: objects.entrySet()) {
            ConfigItem item = storage_.get(e.getKey());
            if (item==null) {
                throw new IllegalArgumentException("item with id "+e.getKey()+" does not exists");
            }
            item.setValue(e.getValue());
        }
    }


    @Override
    public void init()
    {
      if (storage_==null) {
         storage_ = new HashMap<BigDecimal,ConfigItem>();
         ConfigItem item = new ConfigItem();
         BigDecimal id = BigDecimal.valueOf(1L);
         item.setId(id);
         item.setAppName("App1");
         item.setName("StringProperty1");
         item.setMaxLen(64);
         item.setDescription("Example of string property (without first value)");
         item.setType(ConfigItemType.STRING);
         item.setEditable(true);
         storage_.put(id,item);
         item = new ConfigItem();
         id = BigDecimal.valueOf(2L);
         item.setId(id);
         item.setAppName("App1");
         item.setName("second.string");
         item.setDescription("Yet another example of string property");
         item.setType(ConfigItemType.STRING);
         storage_.put(id, item);
         item = new ConfigItem();
         id = BigDecimal.valueOf(3L);
         item.setId(id);
         item.setAppName("App1");
         item.setName("first.int");
         item.setDescription("int property");
         item.setType(ConfigItemType.INTEGER);
         storage_.put(id, item);
         id = BigDecimal.valueOf(4L);
         item = new ConfigItem();
         item.setId(id);
         item.setAppName("App1");
         item.setName("second.int");
         item.setDescription("editable int property");
         item.setType(ConfigItemType.INTEGER);
         item.setEditable(true);
         storage_.put(id, item);

      }
    }

    private static Map<BigDecimal, ConfigItem> storage_;

}
