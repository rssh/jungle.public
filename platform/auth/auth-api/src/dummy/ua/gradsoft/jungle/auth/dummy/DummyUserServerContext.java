
package ua.gradsoft.jungle.auth.dummy;

import java.util.Collection;
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
    
    /**
     * Enoty constructor, required for instantiation from configuration
     *of some application containers. Usially container call setter methods
     *on properties before instantiation. 
     */
    public DummyUserServerContext()
    {}


    /**
     * Constructor, whith all properties.
     * @param login
     * @param password
     * @param properties
     * @param inversePermissions
     * @param permissions
     */
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

    public void setId(String login)
    {  login_=login; }

    public String getPassword()
    {
       return password_;
    }

    public void setPassword(String password)
    {
        password_=password;
    }

    public boolean getInversePermissions()
    {
        return inversePermissions_;
    }


    public void setInversePermissions(boolean value)
    {
      inversePermissions_=value;
    }

    public Collection<String> getPermissions()
    { return permissions_; }

    public void setPermissions(Collection<String> newPermissions)
    {
        permissions_=new TreeSet<String>();
        permissions_.addAll(newPermissions);
    }

    public Map<String,String>  getProperties()
    { return properties_; }

    public void setProperties(Map<String,String> properties)
    {
      properties_=properties;
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



    private String login_;
    private String password_;
    private Map<String,String> properties_;
    private boolean inversePermissions_;
    private Set<String> permissions_;


}
