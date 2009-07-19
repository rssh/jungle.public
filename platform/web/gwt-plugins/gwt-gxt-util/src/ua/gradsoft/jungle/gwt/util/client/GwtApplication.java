
package ua.gradsoft.jungle.gwt.util.client;

import com.extjs.gxt.desktop.client.Desktop;
import com.extjs.gxt.ui.client.event.ComponentEvent;
import com.extjs.gxt.ui.client.event.MenuEvent;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.widget.Window;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.ServiceDefTarget;
import java.util.HashMap;
import java.util.Map;
import ua.gradsoft.jungle.auth.client.AuthClientApiRemoteService;
import ua.gradsoft.jungle.auth.client.AuthClientApiRemoteServiceAsync;

/**
 *Incapsulate common properties of our gwt application.
 * @author rssh
 */
public class GwtApplication {


    public void  initAuth(String authEntryPoint)
    {
      authService_=GWT.create(AuthClientApiRemoteService.class);
      ServiceDefTarget target = (ServiceDefTarget)authService_;
      target.setServiceEntryPoint(authEntryPoint);
    }

    public AuthClientApiRemoteServiceAsync  getAuthService()
    { return authService_; }

    public boolean withDesktop()
    { return desktop_!=null; }

    public void setDesktop(Desktop desktop)
    {
       desktop_=desktop;
    }

    public Desktop getDesktop()
    {
       return desktop_;
    }

    public SelectionListener  getDesktopSelectionListener()
    {
       return desktopSelectionListener_;
    }


    public boolean isLogged()
    { return logged_; }

    
    public void markLogin(String sessionTicket)
    {
      sessionTicket_=sessionTicket;
      logged_=true;
      for(Map.Entry<String,GwtApplicationComponent> e: components_.entrySet()) {
          e.getValue().onLogin(this);
      }
    }
    
    public String getSessionTicket()
    { return sessionTicket_; }

    public void markLogout()
    {
      for(Map.Entry<String,GwtApplicationComponent> e: components_.entrySet()) {
          e.getValue().onLogout(this);
      }        
      sessionTicket_=null;
      logged_=false;
    }

    public void addComponent(GwtApplicationComponent component)
    {
        components_.put(component.getName(), component);
        component.onRegistered(this);
    }

    public GwtApplicationComponent findComponent(String name)
    {
      return components_.get(name);
    }

    public GwtApplicationComponent removeComponent(String name)
    {
      GwtApplicationComponent retval = components_.remove(name);
      retval.onUnregistered(this);
      return retval;
    }

    public Resolution  getCurrentResolution()
    {
      return GwtUtils.getCurrentResolution();  
    }


    private HashMap<String,GwtApplicationComponent> components_ = 
                                     new HashMap<String,GwtApplicationComponent>();



    private Desktop   desktop_ = null;
    private SelectionListener  desktopSelectionListener_ = new SelectionListener()
    {
          @Override
          public void componentSelected(ComponentEvent ce) {
              Window w = null;
              if (ce instanceof MenuEvent) {
                  MenuEvent me = (MenuEvent)ce;
                  me.getItem().getData("window");
              } else {
                  w = ce.getComponent().getData("window");
              }
              if (w!=null) {
                  if (!desktop_.getWindows().contains(w)) {
                     desktop_.addWindow(w);
                  }
                  if (!w.isVisible()) {
                    w.show();
                  }else{
                    w.toFront();
                  }
              }
          }
    };

    private String    sessionTicket_=null;
    private boolean   logged_=false;

    private AuthClientApiRemoteServiceAsync authService_=null;

}
