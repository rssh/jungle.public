
package ua.gradsoft.jungle.auth.client;

import com.google.gwt.user.client.rpc.AsyncCallback;
import java.util.Map;

/**
 *Async interface for AuthClientApi
 * @author rssh
 */
public interface AuthClientApiRemoteServiceAsync {

  void  getSessionTicket(String authType, Map<String,String> parameters,
                                 AsyncCallback<String> callback);

  void  getUserInfo(String sessionTicket, AsyncCallback<String> callback);

}
