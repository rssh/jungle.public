
package ua.gradsoft.jungle.auth.client;

import com.google.gwt.user.client.rpc.AsyncCallback;
import java.util.List;
import java.util.Map;

/**
 *Async interface for AuthClientApi
 * @author rssh
 */
public interface AuthClientApiRemoteServiceAsync  {

  void  getSessionTicket(String authType, Map<String,String> parameters,
                                 AsyncCallback<String> asyncCallback);

  void  getOrRestoreSessionTicket(String authType, Map<String,String> parameters,
                                 AsyncCallback<String> asyncCallback);


  void  getUserInfo(String sessionTicket, AsyncCallback<ClientUserInfo> callback);

  void  checkUserPermission(String sessionTicket, String permission,
                        Map<String,String> parameters,
                        AsyncCallback<Boolean> callback);

   /**
   *@see AuthClientApi#checkUserPermissions(java.lang.String, java.util.List, java.util.Map)
   */
  void  checkUserPermissions(String sessionTicket, List<String> permissions,
                              AsyncCallback<Map<String,Boolean>> callback)
                                      throws InvalidSessionTicketException,
                                             AuthException;


  void  logout(AsyncCallback<Void> callback);

}
