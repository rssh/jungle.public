
package ua.gradsoft.jungle.auth.client.admin;

import java.util.List;
import java.util.Map;

/**
 *Generic api for administration.
 * @author rssh
 */
public interface AuthAdminApi
{

    List<AuthAdminParameterDescription>  getAuthInfoDescription();

    Map<String,String>  getAuthAdminInfo(String userId)
             throws InvalidUserIdException, InvalidAdminParametersException;

    void  setAuthAdminInfo(String userId, Map<String,String> parameters)
            throws InvalidUserIdException, InvalidAdminParametersException;

}
