
package ua.gradsoft.jungle.localization;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 *Annotations, which shows
 *<ul>
 *  <li> POJO class for translations of fields of annotated class, is this is class annotation </li>
 *  <li> that annotated field (or getter) is translated with POJO class with same id for field and method annotations </li>
 * </ul>
 *Example of usage:
 *<pre>
 *@Entity
 *@WithTranslation(CountryTranslations.class)
 *public class Country extends JPACloneableEntity
 * {
 *   @Id
 *   public String getIsoCode()
 *   {return isoCode_;}
 *
 *   public void setIsoCode(String isoCode)
 *   {isoCode_=isoCode;}
 *
 *   @Column(name="name");
 *   @WithTranslations
 *   public String getName()
 *   { return name_; }
 *
 *   public void setName(String name)
 *   { name_=name; }
 *
 *   private String isoCode_;
 *   private String name_;
 * }
 *
 *</pre>
 * @author rssh
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface WithTranslations {

    Class value() default Void.class;

    Class subset() default Void.class;
    
}
