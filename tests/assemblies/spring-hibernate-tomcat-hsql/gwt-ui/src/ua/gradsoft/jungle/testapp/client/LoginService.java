
package ua.gradsoft.jungle.testapp.client;

import com.extjs.gxt.desktop.client.Desktop;
import com.extjs.gxt.desktop.client.StartMenu;
import com.extjs.gxt.ui.client.Style.HorizontalAlignment;
import com.extjs.gxt.ui.client.event.ComponentEvent;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.widget.MessageBox;
import com.extjs.gxt.ui.client.widget.Window;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.form.FormPanel;
import com.extjs.gxt.ui.client.widget.form.TextField;
import com.extjs.gxt.ui.client.widget.menu.MenuItem;
import com.google.gwt.user.client.rpc.AsyncCallback;
import java.util.HashMap;
import java.util.Map;
import ua.gradsoft.jungle.gwt.util.client.GwtApplication;
import ua.gradsoft.jungle.gwt.util.client.GwtApplicationComponent;
import ua.gradsoft.jungle.gwt.util.client.GwtUtils;

/**
 *Login form
 * @author rssh
 */
public class LoginService extends GwtApplicationComponent
{

   public String getName()
   { return "Login"; }


   @Override
   public void onRegistered(GwtApplication application)
   {
     application_=application;

     Window loginWindow = createLoginWindow();
     MenuItem menuItem = new MenuItem("Login");
     menuItem.setData("window", loginWindow);
     menuItem.addSelectionListener(application.getDesktopSelectionListener());
     menuItem.setItemId("id-login");
     application.getDesktop().getStartMenu().add(menuItem);    

   }

   void initPanel(FormPanel panel, Window topLoginFormWindow)
   {
      topLoginFormWindow_=topLoginFormWindow;   

      panel.setHeading("Login");
      panel.setFrame(true);
      panel.setWidth(30*GwtUtils.getCurrentResolution().getXWidth());

      final TextField<String> username = new TextField<String>();
      username.setFieldLabel("Username");
      username.setAllowBlank(false);
      username.setMaxLength(14);
      panel.add(username);

      final TextField<String> password = new TextField<String>();
      password.setFieldLabel("Password");
      password.setAllowBlank(false);
      password.setPassword(true);
      password.setMaxLength(14);
      panel.add(password);


      panel.setButtonAlign(HorizontalAlignment.CENTER);

      Button loginButton = new Button("Login");
      loginButton.addSelectionListener(
              new SelectionListener<ComponentEvent>() {
               public void componentSelected(ComponentEvent e) {                   
                   login_=username.getValue();
                   password_=password.getValue();
                   Map<String,String> loginParams = new HashMap<String,String>();
                   loginParams.put("username", login_);
                   loginParams.put("password", password_);
                   application_.getAuthService().getSessionTicket("plain",
                           loginParams, new AsyncCallback<String>(){
                       public void onFailure(Throwable caught) {
                           MessageBox.alert("Login failed.", caught.getMessage(), null);
                       }

                       public void onSuccess(String sessionTicket) {                           
                           sessionTicket_=sessionTicket;
                           topLoginFormWindow_.close();
                           Desktop desktop = application_.getDesktop();
                           if (desktop!=null) {
                             loginToLogout(desktop.getStartMenu());
                           }
                           application_.markLogin(sessionTicket_);
                       }

                   }
                   );                   
               }
      }
              );

      panel.addButton(loginButton);

      Button cancelButton = new Button("Cancle");
      cancelButton.addSelectionListener(
              new SelectionListener<ComponentEvent>() {
               public void componentSelected(ComponentEvent e) {                  
                  topLoginFormWindow_.close();
               }
      }
              );

      panel.addButton(cancelButton);

   }

   public void   logout()
   {
     application_.getAuthService().logout(
             new AsyncCallback<Void>(){

            public void onFailure(Throwable caught) {
                MessageBox.alert("", "error during logout:"+caught.getMessage(), null);
            }

            public void onSuccess(Void result) {
                sessionTicket_=null;
                application_.markLogout();
            }

     }
             );
   }

   public String getSessionTicket()
   { return sessionTicket_; }

  private void loginToLogout(StartMenu startMenu)
  {
    MenuItem item = (MenuItem)startMenu.getItemByItemId("id-login");
    item.setText("Logout");
    item.setData("window", null);
    item.addSelectionListener(
            new SelectionListener<ComponentEvent>() {
            @Override
            public void componentSelected(ComponentEvent ce) {
                Desktop desktop = application_.getDesktop();
                logout();
                if (desktop==null) {
                    return;
                }                          
                MenuItem item = (MenuItem)desktop.getStartMenu().getItemByItemId("id-login");
                item.setText("Login");
                item.removeSelectionListener(this);
                item.setData("window", topLoginFormWindow_);             
            }
    }
    );
  }

  private Window createLoginWindow()
  {
      Window w = new Window();
      w.setAutoWidth(true);
      FormPanel loginPanel = new FormPanel();
      initPanel(loginPanel, w);
      w.add(loginPanel);
      return w;
  }



   private String login_;
   private String password_;
   private String sessionTicket_;
   private GwtApplication application_;
   private Window  topLoginFormWindow_;
  

}
