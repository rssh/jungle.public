package ua.gradsoft.jungle.testapp.client;

import com.extjs.gxt.desktop.client.Desktop;
import com.extjs.gxt.desktop.client.StartMenu;
import com.extjs.gxt.desktop.client.TaskBar;
import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import ua.gradsoft.jungle.configuration.client.ConfigurationUI;
import ua.gradsoft.jungle.gwt.util.client.GwtApplication;


public class GwtUI implements EntryPoint
{

  private GwtApplication application;
  private Desktop desktop = null;

  public void onModuleLoad() {

       desktop = new Desktop();


       System.err.println("style is:"+desktop.getDesktop().getStyleName());

       application = new GwtApplication();
       application.setDesktop(desktop);
       application.initAuth(GWT.getModuleBaseURL()+"/GWT-RPC/auth");

       ConfigurationUI configurationUI = new ConfigurationUI(
                      GWT.getModuleBaseURL()+"/GWT-RPC/ConfigurationFacade");


       LoginService loginService = new LoginService();
          
       TaskBar taskbar = desktop.getTaskBar();
       
       StartMenu menu = taskbar.getStartMenu();
       menu.setHeading("test-ui");
       menu.setIconStyle("user");

       application.addComponent(configurationUI);
       application.addComponent(loginService);

       
       // does not touch RootPanel, desktop use div 'x-desktop'.
  }




}

