package ua.gradsoft.jungle.auth.dummy;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import ua.gradsoft.jungle.auth.client.AuthException;
import ua.gradsoft.jungle.auth.client.RedirectException;
import ua.gradsoft.jungle.auth.server.AuthServerApiProvider;
import ua.gradsoft.jungle.auth.server.UserServerContext;

/**
 * DummyAuthServerApi provider - dummy provider for mocks
 * Typical spring configuration:
 *<pre>
 *    &lt;bean id="DummyAuthApiProvider"
           class="ua.gradsoft.jungle.auth.dummy.DummyAuthServerApiProvider"
          >
     &lt;property name="users">
       &lt;list>
         &lt;bean class="ua.gradsoft.jungle.auth.dummy.DummyUserServerContext">
           &lt;property name="id" value="anonimous" />
           &lt;property name="inversePermissions" value="false" />
           &lt;property name="permissions">
              &lt;list>
                &lt;value>jungle.parties.read&lt;/value>
                &lt;value>jungle.parties.write&lt;/value>
                &lt;value>jungle.person.read&lt;/value>
                &lt;value>jungle.person.write&lt;/value>
              &lt;/list>
           &lt;/property>
         &lt;/bean>
 *          &lt;bean class="ua.gradsoft.jungle.auth.dummy.DummyUserServerContext">
           &lt;property name="id" value="a1" />
           &lt;property name="password" value="a1" />
           &lt;property name="permissions">
             &lt;list>
                &lt;value>jungle.parties.read&lt;/value>
                &lt;value>jungle.parties.write&lt;/value>
                &lt;value>jungle.person.read&lt;/value>
                &lt;value>jungle.person.write&lt;/value>
             &lt;/list>
           &lt;/property>
         &lt;/bean>
       &lt;/list>
     &lt;/property>
   &lt;/bean>
 *</pre>
 */
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

    /**
     * Add user to instance of provider.  Usefull fr call from configuration for
     * initializing of test set of users.
     * @param name
     * @param inversePermission
     * @param permissions
     */
    public void addUser(String name, String password,
                        boolean inversePermission,
                        List<String> permissions)
    {
       users_.put(name, new DummyUserServerContext(name,password,
                                 Collections.<String,String>emptyMap(),
                                 inversePermission,
                                 permissions
                                 )
                  );
    }


    /**
     * Set list of users (instead default).
     * Usefull for configuring inside configuration file.
     * Note, that user with name 'anonimous' must be present in this list.
     */
    public void setUsers(List<DummyUserServerContext> users)
    {
      users_=new TreeMap<String,DummyUserServerContext>();
      for(DummyUserServerContext u: users) {
          users_.put(u.getId(), u);
      }
    }

    /**
     * Set permission to user with given name.
     * Usefull for call from configuration for initializing of test set of users.
     */
    public void setPermissionsForUser(String name, boolean inversePermission,
                               List<String> permissions)
    {
        DummyUserServerContext usc = users_.get(name);
        if (usc==null) {
            throw new RuntimeException("User with name "+name+" is not found");
        }
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
                                           false, Arrays.asList("jungle.configuration.read",
                                                                "x2","x3")
                                                                )
                );

        users_.put("a2",
                new DummyUserServerContext("a2","a2",
                                           Collections.<String,String>emptyMap(),
                                           false, Arrays.asList("jungle.configuration.read",
                                                                "jungle.configuration.write",
                                                                "x2","x3")
                                                                )
                );


    }

}

