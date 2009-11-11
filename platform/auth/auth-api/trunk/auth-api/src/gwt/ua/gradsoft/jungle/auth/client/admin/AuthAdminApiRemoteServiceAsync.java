
package ua.gradsoft.jungle.auth.client.admin;

import com.google.gwt.user.client.rpc.AsyncCallback;
import java.util.List;
import java.util.Map;

/**
 *Async interface to auth admin API
 * @author rssh
 */
public interface AuthAdminApiRemoteServiceAsync {


    void getAuthInfoDescription(AsyncCallback<List<AuthAdminParameterDescription>> callback);


    void  getAuthAdminInfo(String userId, AsyncCallback<Map<String,String>> callback);


    void  setAuthAdminInfo(String userId, Map<String,String> parameters, AsyncCallback<Void> callback);

}
