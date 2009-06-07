
package ua.gradsoft.jungle.gwt.util.client;

import com.extjs.gxt.desktop.client.Desktop;
import java.util.HashMap;
import java.util.Map;

/**
 *Incapsulate common properties of our gwt application.
 * @author rssh
 */
public class GwtApplication {

    public boolean withDesktop()
    { return desktop_!=null; }

    public void setDesktop(Desktop desktop)
    {
       desktop_=desktop;
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

    private String    sessionTicket_=null;
    private boolean   logged_=false;


}
