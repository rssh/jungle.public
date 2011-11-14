
package ua.gradsoft.jungle.gwt.util.client;

import java.util.HashMap;
import java.util.Map;

import ua.gradsoft.jungle.auth.client.AuthClientApiRemoteService;
import ua.gradsoft.jungle.auth.client.AuthClientApiRemoteServiceAsync;

import com.extjs.gxt.desktop.client.Desktop;
import com.extjs.gxt.ui.client.event.ComponentEvent;
import com.extjs.gxt.ui.client.event.MenuEvent;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.widget.Component;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.Layout;
import com.extjs.gxt.ui.client.widget.Status;
import com.extjs.gxt.ui.client.widget.Window;
import com.extjs.gxt.ui.client.widget.layout.CardLayout;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.extjs.gxt.ui.client.widget.menu.MenuBar;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.ServiceDefTarget;

/**
 *Incapsulate common properties of our gwt application.
 * @author rssh
 */
public class GwtApplication {


    /**
     * set entry point for Auth API. Usially called during initialization.
     * @param authEntryPoint entry point for auth remote service.
     */
    public void  initAuth(String authEntryPoint)
    {
      authService_=GWT.create(AuthClientApiRemoteService.class);
      ServiceDefTarget target = (ServiceDefTarget)authService_;
      target.setServiceEntryPoint(authEntryPoint);
    }

    /**
     * get auth service, previously created during initAuth
     * @return authSerive or null, if one is not yet initialized.
     */
    public AuthClientApiRemoteServiceAsync  getAuthService()
    { return authService_; }

    /**
     *  if this is application on desktop ?
     * @return true if this application have desktop, otherwise false.
     */
    public boolean withDesktop()
    { return desktop_!=null; }

    /**
     * set Desktop for application. Usually called during initialization.
     * @param desktop - desktop to set.
     */
    public void setDesktop(Desktop desktop)
    {
       desktop_=desktop;
    }

    /**
     * get desktop, if one was set, otherwise - null.
     * @return
     */
    public Desktop getDesktop()
    {
       return desktop_;
    }


    /**
     * get selection listener for desktop, which brings to from
     *windows, which is setted as "window" data in component.
     * @return selection listener.
     */
    public SelectionListener<ComponentEvent>  getDesktopCeSelectionListener()
    {
       return desktopCeSelectionListener_;
    }

    /**
     * get selection listener for desktop, which brings to from
     *windows, which is setted as "window" data in menu item.
     * @return selection listener.
     */
    public SelectionListener<MenuEvent>  getDesktopMeSelectionListener()
    {
       return desktopMeSelectionListener_;
    }

    /**
     * if this application have common menu bar ?
     * @return true if application have common menu bar.
     */
    public boolean withMenuBar()
    {
       return menuBar_!=null;
    }

    /**
     * set MenuBar for application. Usually called during initialization.
     * @param menuBar - menuBar to set.
     */
    public void setMenuBar(MenuBar menuBar)
    {
       menuBar_=menuBar;
    }

    /**
     *@return common application menubar, if one was set, otherwise - null.
     */
    public MenuBar getMenuBar()
    {
       return menuBar_;
    }                

    /**
     * if this application have common status ?
     * @return true if application have common status.
     */
    public boolean withStatus()
    {
       return status_!=null;
    }


    /**
     * set Status for application. Usually called during initialization.
     * @param status - status to set.
     */
    public void setStatus(Status status)
    {
       status_=status;
    }

    /**
     *@return common application status if one was set, otherwise - null.
     */
    public Status getStatus()
    {
       return status_;
    }


    /**
     * if this application have internal content panel ?
     *   (on which all components will shown)
     * @return true
     */
    public boolean withInternalContentPanel()
    {
       return internalContentPanel_!=null;
    }

    public ContentPanel getInternalContentPanel()
    { return internalContentPanel_; }

    public void         setInternalContentPanel(ContentPanel cn)
    {
      internalContentPanel_=cn;
    }


    public SelectionListener<MenuEvent> getInternalContentPanelMeSelectionListener()
    {
      return cardContentPanelSelectionListener_;
    }


    /**
     * @return true if user is logged into application
     */
    public boolean isLogged()
    { return logged_; }


    /**
     * called by LoginService when we receive sessionTicket
     * @param sessionTicket - ticket to session from login service.
     */
    public void markLogin(String sessionTicket)
    {
      sessionTicket_=sessionTicket;
      logged_=true;
      for(Map.Entry<String,GwtApplicationComponent> e: components_.entrySet()) {
          e.getValue().onLogin();
      }
    }

    /**
     * @return session ticket if user is logged into application.
     */
    public String getSessionTicket()
    { return sessionTicket_; }

    /**
     * logout from application. Called by LoginService.
     */
    public void markLogout()
    {
      for(Map.Entry<String,GwtApplicationComponent> e: components_.entrySet()) {
          e.getValue().onLogout();
      }        
      sessionTicket_=null;
      logged_=false;
    }

    /**
     *add application compoment and call callback function
     * @param component
     */
    public void addComponent(GwtApplicationComponent component)
    {
        components_.put(component.getName(), component);
        component.setGwtApplication(this);
        component.onRegistered();
    }

    /**
     * find application component with name <code> name </code>
     * @param name - name of component to found.
     * @return component if found, otherwise - null.
     */
    public GwtApplicationComponent findComponent(String name)
    {
      return components_.get(name);
    }

    /**
     * remove component with name <code> name </code>
     * @param name
     * @return
     */
    public GwtApplicationComponent removeComponent(String name)
    {
      GwtApplicationComponent retval = components_.remove(name);
      if (retval!=null) {
        retval.onUnregistered();
      }
      return retval;
    }


    private void windowToFrontOnDesktop(Window w)
    {
      if (desktop_!=null && w!=null) {
          if (!desktop_.getWindows().contains(w)) {
              desktop_.addWindow(w);
          }
          if (!w.isVisible()) {
              w.show();
          } else {
              w.toFront();
          }
      }
    }
    
	private void componentToFrontOnInternalContentPanel(Component w) {
		Layout l = internalContentPanel_.getLayout();
		if (l instanceof CardLayout) {
			CardLayout cl = (CardLayout) l;
			cl.setActiveItem(w);
		} else if (l instanceof FitLayout) {
			internalContentPanel_.removeAll();
			internalContentPanel_.add(w);
		} else {
			throw new IllegalStateException("InternalContentPanel must have card layout");
		}
	}

	private HashMap<String, GwtApplicationComponent> components_ = new HashMap<String, GwtApplicationComponent>();



    private Desktop   desktop_ = null;
    private Status status_ = null;
    private MenuBar menuBar_ = null;
    private ContentPanel internalContentPanel_=null;
    //private CardLayout   internalCardLayout_=null;



    private SelectionListener<ComponentEvent>  desktopCeSelectionListener_ = new SelectionListener<ComponentEvent>()
    {
          @Override
          public void componentSelected(ComponentEvent ce) {
              Window w = ce.getComponent().getData("window");
              windowToFrontOnDesktop(w);
          }
    };

    private SelectionListener<MenuEvent>  desktopMeSelectionListener_ = new SelectionListener<MenuEvent>() {
          @Override
          public void componentSelected(MenuEvent me) {
              Window w = me.getItem().getData("window");
              windowToFrontOnDesktop(w);
          }
    };


    private SelectionListener<MenuEvent>  cardContentPanelSelectionListener_ = new SelectionListener<MenuEvent>() {
        @Override
        public void componentSelected(MenuEvent me) {
            Component c = me.getItem().getData("component");
            componentToFrontOnInternalContentPanel(c);
        }
    };


    private String    sessionTicket_=null;
    private boolean   logged_=false;

    private AuthClientApiRemoteServiceAsync authService_=null;

}
