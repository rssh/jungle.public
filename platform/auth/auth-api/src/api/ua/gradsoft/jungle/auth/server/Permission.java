
package ua.gradsoft.jungle.auth.server;

import java.lang.annotation.Documented;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;




/**
 *Permission for call of some server-side method.
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Inherited()
public @interface Permission {

    /**
     * name of permission. Can be set as '*' to mark any permission.
     */
    public String name();

    /**
     * Optional permission arguments (if one exists).
     * Arguments must be set of pairs(name,value), where we can refer to
     * method parameters in value (as $0, $1, ... etc). 
     */
    public String[] arguments() default {};


}

