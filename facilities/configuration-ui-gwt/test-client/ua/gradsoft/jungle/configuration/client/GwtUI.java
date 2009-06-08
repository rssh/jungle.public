package ua.gradsoft.jungle.configuration.client;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.Widget;
import ua.gradsoft.jungle.gwt.util.client.GwtApplication;

/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class GwtUI implements EntryPoint {

  /**
   * This is the entry point method.
   */
  public void onModuleLoad() {

    application_ = new GwtApplication();
    application_.initAuth(GWT.getModuleBaseURL()+"/auth");

    ConfigurationUI ui = new ConfigurationUI(GWT.getModuleBaseURL()+"/TestConfiguration");
    application_.addComponent(ui);

    Widget w = ui.getTableWidget("app2");    
    RootPanel.get().add(w);
    RootPanel.get().setVisible(true);


  }

  private GwtApplication application_;

}
