package ua.gradsoft.jungle.testapp.client;

import com.extjs.gxt.desktop.client.Desktop;
import com.extjs.gxt.desktop.client.Shortcut;
import com.extjs.gxt.desktop.client.StartMenu;
import com.extjs.gxt.desktop.client.TaskBar;
import com.extjs.gxt.ui.client.event.ComponentEvent;
import com.extjs.gxt.ui.client.event.MenuEvent;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.widget.Window;
import com.extjs.gxt.ui.client.widget.Window.CloseAction;
import com.extjs.gxt.ui.client.widget.menu.MenuItem;
import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.Widget;
import ua.gradsoft.jungle.configuration.client.ConfigurationUI;


public class GwtUI implements EntryPoint
{

  private Desktop desktop = new Desktop();

  public void onModuleLoad() {


      SelectionListener<ComponentEvent> listener = new SelectionListener<ComponentEvent>()
      {
          @Override
          public void componentSelected(ComponentEvent ce) {
              Window w = null;
              if (ce instanceof MenuEvent) {
                  MenuEvent me = (MenuEvent)ce;
                  w = me.item.getData("window");
              } else {
                  w = ce.component.getData("window");
              }
              if (!desktop.getWindows().contains(w)) {
                  desktop.addWindow(w);
              }
              if (w!=null && !w.isVisible()) {
                  w.show();
              }else{
                  w.toFront();
              }
          }
      };



       ConfigurationUI configurationUI = new ConfigurationUI();
       configurationUI.init(GWT.getModuleBaseURL()+
                              "/GWT-RPC/ConfigurationFacade");

       Window configurationWindow = createConfigurationWindow(configurationUI);
       //Widget w = configurationUI.getTableWidget("app2");
     
       Shortcut s1 = new Shortcut();
       s1.setText("configuration");
       s1.setId("configuration-win-shortcut");
       s1.setData("window",configurationWindow);
       s1.addSelectionListener(listener);
       desktop.addShortcut(s1);
       
       TaskBar taskbar = desktop.getTaskBar();
       
       StartMenu menu = taskbar.getStartMenu();
       menu.setHeading("test-ui");
       menu.setIconStyle("user");
       
       MenuItem menuItem = new MenuItem("Configuration");
       menuItem.setData("window", configurationWindow);
       menuItem.addSelectionListener(listener);
       menu.add(menuItem);
       
       // does not touch RootPanel, desktop use div 'x-desktop'.
  }


  private Window createConfigurationWindow(ConfigurationUI configurationUI)
  {
      Window w = new Window();
      w.setCloseAction(CloseAction.CLOSE);
      w.setMinimizable(true);
      w.setMaximizable(true);
      w.setHeading("Configuration for app2");
      Widget tableWidget = configurationUI.getTableWidget("app2");
      w.add(tableWidget);
      return w;
  }

}

