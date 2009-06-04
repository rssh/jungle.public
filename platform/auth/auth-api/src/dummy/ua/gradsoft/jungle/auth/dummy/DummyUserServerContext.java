
package ua.gradsoft.jungle.auth.dummy;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import ua.gradsoft.jungle.auth.server.UserServerContext;

/**
 *Dummy user server context
 * @author rssh
 */
public class DummyUserServerContext implements UserServerContext
{

    public DummyUserServerContext(String login, String password,
                                  Map<String,String> properties,
                                  boolean inversePermissions,
                                  List<String> permissions)
    {
     login_=login;
     password_=password;
     properties_=properties;
     inversePermissions_=inversePermissions;
     permissions_=new TreeSet<String>();
     permissions_.addAll(permissions);
    }


    public String getId() {
        return login_;
    }

    public String getProperty(String propertyName) {
        if (propertyName.equals("username")) {
            return login_;
        }
        return properties_.get(propertyName);
    }

    // does not use params, only
    public boolean checkPermission(String permissionName, Map<String, String> permissionArguments) {
        if (!inversePermissions_) {
            return !permissions_.contains(permissionName);
        } else {
            return permissions_.contains(permissionName);
        }
    }

    boolean checkPassword(String password)
    {
      return password_.equals(password);
    }

    private String login_;
    private String password_;
    private Map<String,String> properties_;
    private boolean inversePermissions_;
    private Set<String> permissions_;

}