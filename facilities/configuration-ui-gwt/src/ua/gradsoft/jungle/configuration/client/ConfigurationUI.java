package ua.gradsoft.jungle.configuration.client;

import com.extjs.gxt.desktop.client.Desktop;
import com.extjs.gxt.desktop.client.StartMenu;
import com.extjs.gxt.ui.client.widget.MessageBox;
import com.extjs.gxt.ui.client.widget.Window;
import com.extjs.gxt.ui.client.widget.Window.CloseAction;
import com.extjs.gxt.ui.client.widget.menu.Item;
import com.extjs.gxt.ui.client.widget.menu.MenuItem;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.rpc.ServiceDefTarget;
import com.google.gwt.user.client.ui.Widget;
import java.util.HashMap;
import ua.gradsoft.jungle.gwt.util.client.GwtApplication;
import ua.gradsoft.jungle.gwt.util.client.GwtApplicationComponent;

/**
 *'API Entry point' for configuration unit.
 * @author rssh
 */
public class ConfigurationUI extends GwtApplicationComponent
{

    public ConfigurationUI(String entryPoint)
    { entryPoint_=entryPoint; }


    @Override
    public String getName()
    { return "ConfigurationUI"; }



    /**
     * Initialize remote API.  must be called before any usage of this class.   
     */
    @Override
    public void onRegistered(GwtApplication application)
    {
      service_ = GWT.create(ConfigurationRemoteService.class);
      if (entryPoint_!=null) {
       ServiceDefTarget target = (ServiceDefTarget)service_;
       target.setServiceEntryPoint(entryPoint_);
      }
    }

    @Override
    public void onLogin(GwtApplication application)
    {
      final GwtApplication fApplication = application;

      readAccessReaded_=false;
      application.getAuthService().checkUserPermission(
              application.getSessionTicket(), 
              "jungle.configuration.read", new HashMap<String,String>(),
              new AsyncCallback<Boolean>()
      {
            public void onFailure(Throwable caught) {
                MessageBox.alert("Failure:","Can't check user permission:"+caught.getMessage(), null);
            }

            public void onSuccess(Boolean result) {
                readAccess_=result;
                if (readAccess_) {
                    Desktop desktop = fApplication.getDesktop();
                    if (desktop==null) return;
                    StartMenu startMenu = desktop.getStartMenu();
                    Item i = startMenu.getItemByItemId("id-ConfigurationUI");
                    if (i==null) {
                        MenuItem mi = new MenuItem("Configuration");
                        mi.setData("window", getTableWindow("app2"));
                        mi.addSelectionListener(fApplication.getDesktopSelectionListener());
                        mi.setItemId("id-ConfigurationUI");
                        startMenu.add(mi);
                    }
                }
                readAccessReaded_=true;
            }          
      }
      );  
      
      writeAccessReaded_=false;
      application.getAuthService().checkUserPermission(
              application.getSessionTicket(), "jungle.configuration.write",
              new HashMap<String,String>(), 
              new AsyncCallback<Boolean>() {

            public void onFailure(Throwable caught) {
                MessageBox.alert("Failure:","Can't check user permission:"+caught.getMessage(), null);
            }

            public void onSuccess(Boolean result) {
                writeAccess_=result;
                writeAccessReaded_=true;
            }
          
      }
              );

    }

    @Override
    public void onLogout(GwtApplication application) {
        Desktop desktop = application.getDesktop();
        if (desktop!=null) {
            StartMenu startMenu = desktop.getStartMenu();
            Item i = startMenu.getItemByItemId("id-ConfigurationUI");
            if (i!=null) {
                startMenu.remove(i);
            }
        }
        readAccessReaded_=false;
        readAccess_=false;
        writeAccessReaded_=false;
        writeAccess_=false;
    }

    public boolean withReadAccess()
    {
       return readAccessReaded_ && readAccess_;
    }


    public boolean withWriteAccess()
    {
       return writeAccessReaded_ && writeAccess_;
    }

    public ConfigurationRemoteServiceAsync  getService()
    { return service_; }


    public ConfigItemsTableWidget getTableWidget(String appName)
    {
      return new ConfigItemsTableWidget(this,appName);
    }

    public Window getTableWindow(String appName)
    {
        Window w = new Window();
        w.setCloseAction(CloseAction.CLOSE);
        w.setMinimizable(true);
        w.setMaximizable(false);
        w.setHeading("Configuration for app2");
        Widget tableWidget = getTableWidget(appName);
        w.add(tableWidget);
        return w;
    }

    private ConfigurationRemoteServiceAsync service_=null;
    private String                          entryPoint_=null;
    private boolean                         readAccess_=false;
    private boolean                         readAccessReaded_=false;
    private boolean                         writeAccess_=false;
    private boolean                         writeAccessReaded_=false;

}
