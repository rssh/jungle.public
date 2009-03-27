package ua.gradsoft.jungle.testapp.client;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.Widget;
import ua.gradsoft.jungle.configuration.client.ConfigurationUI;


public class GwtUI implements EntryPoint
{
  public void onModuleLoad() {
     ConfigurationUI configurationUI = new ConfigurationUI();
     configurationUI.init(GWT.getModuleBaseURL()+
                              "/GWT-RPC/ConfigurationFacade");
     Widget w = configurationUI.getTableWidget("app2");
     RootPanel.get().add(w);
     RootPanel.get().setVisible(true);
  }
}

