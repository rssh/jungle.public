package ua.gradsoft.jungle.auth.server;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 *Annotations (usially method) which shows, that for
 * calling method, client require set of permissions 
 * (specified in <code>permissions</code> field).
 *<p>
 *<pre>
 *&064;Require(
 *   permissions={
 *     &064;Permission(name="organizations.edit.all"),
 *     &064;Permission(name="organizations.edit.my",
 *                     arguments={"organizationId","$0"})
 *   }
 *)
 *void editOrganization(BigDecimal organizationId)
 *</pre>
 *
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
public @interface Require {

    public Permission[] permissions();

}
