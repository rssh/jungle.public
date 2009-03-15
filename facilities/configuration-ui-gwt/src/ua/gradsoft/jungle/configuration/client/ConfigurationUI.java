package ua.gradsoft.jungle.configuration.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.ServiceDefTarget;

/**
 *'API Entry point' for configuration unit.
 * @author rssh
 */
public class ConfigurationUI {



    /**
     * Initialize remote API.  must be called before any usage of this class.
     * @param entryPointUri - address for RPC entry point.
     */
    public void init(String entryPointUri)
    {
      service_ = GWT.create(ConfigurationRemoteService.class);
      if (entryPointUri!=null) {
       ServiceDefTarget target = (ServiceDefTarget)service_;
       target.setServiceEntryPoint(entryPointUri);
      }
    }

    public ConfigurationRemoteServiceAsync  getService()
    { return service_; }
    
    public ConfigItemsTableWidget getTableWidget(String appName)
    {
      return new ConfigItemsTableWidget(this,appName);
    }


    private ConfigurationRemoteServiceAsync service_=null;

}
