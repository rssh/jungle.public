package ua.gradsoft.jungle.auth.client;

import java.util.Map;

/**
 * API, which aviable to auth client.
 **/ 
public interface AuthClientApi
{

  /**
   * get Session ticket by authType and set of parameters.
   *If authentication process requires redirect to other resources
   *(such as openid), than throw redirect exception.
   *some of predefined auth types:
   *<ul>
   * <li> Auth-type: "plain" </li>
   * <ul> parameters:
   *   <li> "username" - name of user to login </li>
   *   <li> "password" - password to login </li>
   *   <li> example parameters string: 
   *         "username=myname&amp;password=myPassword" </li>
   * </ul>
   * <li> Auth-type: "md5pwd" </li>
   * <ul> parameters:
   *   <li> "username" -name of user to login </li>
   *   <li> "pwdhash" - md5 hash of password </li>
   * </ul>
   * <li> Auth-type: "openid" </li>
   * <ul> parameters:
   *   <li> "url" - uri to check </li>
   * </ul>
   *</ul>
   * Implementations can support only some of this auth types or
   *  provide own.
   *
   *@param  authType - one of predefined authTypes,
   *@param  parameters query (depend from auth-type) 
   *@return session ticket
   **/ 
  public  String  getSessionTicket(String authType, Map<String,String> parameters)
                                                 throws 
                                                   RedirectException,
                                                   AuthException;

  /**
   * get minimal client user info (is aviable).
   **/ 
  public ClientUserInfo  getUserInfo(String sessionTicket)
                                   throws InvalidSessionTicketException,
                                          AuthException;


  /**
   * Check permission with name <code> permission </code>
   * @param sessionTicket - session ticket, received by <code> getSessionTicket </code>
   * @param permission - permission to check
   * @param params - permission parameters to check
   * @return true if user have such permission, otherwise false
   * @throws ua.gradsoft.jungle.auth.client.InvalidSessionTicketException
   * @throws ua.gradsoft.jungle.auth.client.AuthException
   */
  public boolean checkUserPermission(String sessionTicket, String permission,
                                     Map<String,String> params)
                                      throws InvalidSessionTicketException,
                                             AuthException;


  /**
   * If this API holds last session ticked, to track client, then
   *  remove all internal session state.
   */
  public void logout();

}
