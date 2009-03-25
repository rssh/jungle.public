package ua.gradsoft.jungle.configuration.client;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class GwtUI implements EntryPoint {

  /**
   * This is the entry point method.
   */
  public void onModuleLoad() {

    ConfigurationUI ui = new ConfigurationUI();
    ui.init(GWT.getModuleBaseURL()+"/TestConfiguration");

    Widget w = ui.getTableWidget("app2");    
    RootPanel.get().add(w);
    RootPanel.get().setVisible(true);


  }


}
