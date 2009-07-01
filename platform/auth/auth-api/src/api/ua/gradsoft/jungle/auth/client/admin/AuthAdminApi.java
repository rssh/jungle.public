
package ua.gradsoft.jungle.auth.client.admin;

import java.util.List;
import java.util.Map;
import ua.gradsoft.jungle.auth.server.Permission;

/**
 *Generic api for administration.
 * @author rssh
 */
public interface AuthAdminApi
{

   @Permission(name="jungle.auth.admin")
    List<AuthAdminParameterDescription>  getAuthInfoDescription();

    @Permission(name="jungle.auth.admin")
    Map<String,String>  getAuthAdminInfo(String userId)
             throws InvalidUserIdException, InvalidAdminParametersException;

    @Permission(name="jungle.auth.admin")
    void  setAuthAdminInfo(String userId, Map<String,String> parameters)
            throws InvalidUserIdException, InvalidAdminParametersException;

}
