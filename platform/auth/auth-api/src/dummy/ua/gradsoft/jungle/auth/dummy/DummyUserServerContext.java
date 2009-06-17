
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
        boolean retval;
        System.err.println("check permission "+permissionName+" for user "+login_);
        if (inversePermissions_) {
            retval=!permissions_.contains(permissionName);
        } else {
            retval=permissions_.contains(permissionName);
        }
        System.err.println("return "+retval);
        return retval;
    }


    boolean checkPassword(String password)
    {
      return password_.equals(password);
    }


    // configuration stuff.
    void setInversePermissions(boolean newInversePermissions)
    {
      inversePermissions_=true;
    }

    void setPermissions(List<String> newPermissions)
    {
        permissions_=new TreeSet<String>();
        permissions_.addAll(newPermissions);
    }

    private String login_;
    private String password_;
    private Map<String,String> properties_;
    private boolean inversePermissions_;
    private Set<String> permissions_;


}
