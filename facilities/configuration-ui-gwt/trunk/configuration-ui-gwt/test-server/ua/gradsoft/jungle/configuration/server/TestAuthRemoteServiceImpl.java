
package ua.gradsoft.jungle.configuration.server;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import java.util.Map;
import ua.gradsoft.jungle.auth.client.AuthClientApiRemoteService;
import ua.gradsoft.jungle.auth.client.AuthException;
import ua.gradsoft.jungle.auth.client.ClientUserInfo;
import ua.gradsoft.jungle.auth.client.InvalidSessionTicketException;
import ua.gradsoft.jungle.auth.client.RedirectException;

/**
 *Test  server for auth
 * @author rssh
 */
public class TestAuthRemoteServiceImpl extends RemoteServiceServlet
                                        implements AuthClientApiRemoteService
{

    /**
     * all users have all permissions.
     */
    public boolean checkUserPermission(String sessionTicket, String permission, Map<String, String> params) throws InvalidSessionTicketException, AuthException {
        return true;
    }

    public String getSessionTicket(String authType, Map<String, String> parameters) throws RedirectException, AuthException {
        return "xxx";
    }

    public ClientUserInfo getUserInfo(String sessionTicket) throws InvalidSessionTicketException, AuthException {
        initFakeUser();
        return fakeUser_;
    }

    public void logout() {
        /* do nothing */
    }

    private void initFakeUser()
    {
        if (fakeUser_==null) {
            fakeUser_=new ClientUserInfo();
            fakeUser_.setNickname("testuser");
        }
    }

    private ClientUserInfo fakeUser_ = null;
}
