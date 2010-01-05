
package ua.gradsoft.jungle.auth.client;

import com.google.gwt.user.client.rpc.RemoteService;
import java.util.Map;

/**
 *Auth API remote service for Gwt clients.
 */
public interface AuthClientApiRemoteService extends RemoteService, AuthClientApi
{

  /**
   *@see AuthClientApi#getSessionTicket(java.lang.String, java.util.Map)
   **/
  public  String  getSessionTicket(String authType, Map<String,String> parameters)
                                                 throws
                                                   RedirectException,
                                                   AuthException;

  /**
   *@see AuthClientApi#getOrRestoreSessionTicket(java.lang.String, java.util.Map) 
   */
  public  String  getOrRestoreSessionTicket(String authType, Map<String,String> parameters)
                                                  throws
                                                   RedirectException,
                                                   AuthException;

  /**
   *@see AuthClientApi#getUserInfo(java.lang.String)
   **/
  public ClientUserInfo  getUserInfo(String sessionTicket)
                                   throws InvalidSessionTicketException,
                                          AuthException;


  /**
   *@see AuthClientApi#checkUserPermission(java.lang.String, java.lang.String, java.util.Map)
   */
  public boolean checkUserPermission(String sessionTicket, String permission,
                                     Map<String,String> params)
                                      throws InvalidSessionTicketException,
                                             AuthException;


  /**
   *@see AuthClientApi#logout()
   */
  public void logout();



}
