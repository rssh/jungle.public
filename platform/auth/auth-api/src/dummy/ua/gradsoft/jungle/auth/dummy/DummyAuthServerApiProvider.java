package ua.gradsoft.jungle.auth.dummy;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import ua.gradsoft.jungle.auth.client.AuthException;
import ua.gradsoft.jungle.auth.client.RedirectException;
import ua.gradsoft.jungle.auth.server.AuthServerApiProvider;
import ua.gradsoft.jungle.auth.server.UserServerContext;

public class DummyAuthServerApiProvider implements AuthServerApiProvider
{

    public UserServerContext findAuthenticatedUserContext(String authType, Map<String, String> namedParams) throws RedirectException, AuthException
    {
        if (!authType.equals("plain")) {
            throw new AuthException("Unknown auth type:"+authType);
        }
        String login = namedParams.get("username");
        String password = namedParams.get("password");
        if (login==null) {
            throw new AuthException("username is not set");
        }
        if (password==null) {
            throw new AuthException("password is not set");
        }
        DummyUserServerContext user = users_.get(login);
        if (user==null) {
            throw new AuthException("no such user");
        }
        if (!user.checkPassword(password)) {
            throw new AuthException("Invalid password");
        }
        return user;
    }

    public UserServerContext findContextById(String userId) throws AuthException {
        UserServerContext retval = users_.get(userId);
        if (retval==null) {
            throw new AuthException("user not found for id "+userId);
        }
        return retval;
    }

    public UserServerContext getAnonimousContext() throws AuthException {
        return users_.get("anonimous");
    }


    private static Map<String,DummyUserServerContext> users_;

    static {
        users_ = new HashMap<String, DummyUserServerContext>();

        users_.put("anonimous",
                   new DummyUserServerContext("anonimous","",
                                              Collections.<String,String>emptyMap(),
                                              false,
                                              Arrays.asList("view.appregistry","x1")
                       )
                   );

        users_.put("a1",
                new DummyUserServerContext("a1","a1",
                                           Collections.<String,String>emptyMap(),
                                           false, Arrays.asList("view.configuration",
                                                                "x2","x3")
                                                                )
                );

        users_.put("a2",
                new DummyUserServerContext("a2","a2",
                                           Collections.<String,String>emptyMap(),
                                           false, Arrays.asList("view.configuration",
                                                                "x2","x3")
                                                                )
                );


    }

}
