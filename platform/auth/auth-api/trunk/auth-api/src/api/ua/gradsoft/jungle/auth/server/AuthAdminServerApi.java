package ua.gradsoft.jungle.auth.server;

import java.util.List;
import java.util.Map;
import ua.gradsoft.jungle.auth.client.admin.AuthAdminApi;
import ua.gradsoft.jungle.auth.client.admin.AuthAdminParameterDescription;
import ua.gradsoft.jungle.auth.client.admin.InvalidAdminParametersException;
import ua.gradsoft.jungle.auth.client.admin.InvalidUserIdException;

/**
 *Server api for auth admin.
 * @author rssh
 */
public interface AuthAdminServerApi extends AuthAdminApi
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
