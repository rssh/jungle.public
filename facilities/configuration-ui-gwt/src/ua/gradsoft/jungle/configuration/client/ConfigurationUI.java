package ua.gradsoft.jungle.configuration.client;

import com.extjs.gxt.desktop.client.Desktop;
import com.extjs.gxt.desktop.client.StartMenu;
import com.extjs.gxt.ui.client.data.BeanModel;
import com.extjs.gxt.ui.client.widget.Component;
import com.extjs.gxt.ui.client.widget.MessageBox;
import com.extjs.gxt.ui.client.widget.Window;
import com.extjs.gxt.ui.client.widget.grid.EditorGrid;
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
        Desktop desktop = application.getDesktop();
        if (desktop!=null) {
          MenuItem mi = new MenuItem("Configuration");
          Window w = getTableWindow("app2");
          mi.setData("window", w);
          mi.addSelectionListener(application.getDesktopSelectionListener());
          mi.setItemId("id-ConfigurationUI");
          mi.disable();
          desktop.getStartMenu().add(mi);
        }
      }
    }

    @Override
    public void onLogin(GwtApplication application)
    {
      final GwtApplication fApplication = application;

      readAccessReaded_=false;
      application.getAuthService().checkUserPermission(
              fApplication.getSessionTicket(),
              "jungle.configuration.read", new HashMap<String,String>(),
              new AsyncCallback<Boolean>()
      {
            public void onFailure(Throwable caught) {
                MessageBox.alert("Failure:","Can't check user permission:"+caught.getMessage() , null);
            }

            public void onSuccess(Boolean result) {
                readAccess_=result;
                readAccessReaded_=true;
                Desktop desktop = fApplication.getDesktop();
                if (desktop!=null) {
                    StartMenu startMenu = desktop.getStartMenu();
                    Component item = startMenu.getItemByItemId("id-ConfigurationUI");
                    if (item!=null) {
                        if (result) {
                            item.enable();
                            if (grid_!=null) {
                                grid_.getStore().getLoader().load();
                            }
                        } else {
                            item.disable();
                        }
                    }
                }
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
            Component i = startMenu.getItemByItemId("id-ConfigurationUI");
            if (i!=null) {
                i.disable();
                ((Window)i.getData("window")).close();
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
        w.setClosable(true);
        w.setMinimizable(true);
        w.setMaximizable(false);
        w.setHeading("Configuration for app2");
        Widget tableWidget = getTableWidget(appName);
        w.add(tableWidget);
        return w;
    }

    public void setGrid(EditorGrid<BeanModel> grid)
    {
      grid_=grid;  
    }

    private ConfigurationRemoteServiceAsync service_=null;
    private String                          entryPoint_=null;
    private boolean                         readAccess_=false;
    private boolean                         readAccessReaded_=false;
    private boolean                         writeAccess_=false;
    private boolean                         writeAccessReaded_=false;

    private EditorGrid<BeanModel>         grid_;

}
