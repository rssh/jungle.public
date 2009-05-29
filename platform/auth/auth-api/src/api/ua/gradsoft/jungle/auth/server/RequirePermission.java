
package ua.gradsoft.jungle.auth.server;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Used to indicate, that method (or class) require permissions.
 *Example of usage:
 *<pre>
 * &064;RequirePermission(name="edit.organization")
 * public void saveOrganization(Organization organization);
 *</pre>
 *which mean, that method <code>saveOrganization</code> must be called
 *only with user with permission <code>edit.organization</code>.
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
public @interface RequirePermission {

    /**
     * name of permission. Can be set as '*' to mark any permission.
     */
    public String name();

    /**
     * Oprional permission arguments (if one exists).
     * Arguments must be set of pairs(name,value), where we can refer to
     * method parameters in value (as $0, $1, ... etc).
     */
    public String[] arguments();


}
