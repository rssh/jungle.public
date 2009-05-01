
package ua.gradsoft.jungle.persistence.jpaex;

import java.util.List;
import javax.persistence.Entity;

/**
 *Utility methods for Jpa
 */
public class JpaHelper {

    /**
     * find first super of origin, annotated with Entity annotation.
     * If origin is annotated, return origin. If annotated class
     * is not found in super-s sequence - return null.
     */
    public static Class findSameOrSuperJpaEntity(Class origin)
    {
      Class curr=origin;
      while(curr!=null) {
          if (curr.isAnnotationPresent(Entity.class)) {
              return curr;
          }
          if (curr.isEnum()) {
              return null;
          }
          curr=curr.getSuperclass();
      }
      return curr;
    }

    /**
     * get list of all JPA properties for class <code> entityClass </code>
     **/
    public static List<JpaEntityProperty> getAllJpaProperties(Class entityClass)
    {
      return JpaEntityProperty.getAllPropertiesForEntity(entityClass);
    }

    /**
     * get Jpa property, with name <code> name </code>
     *@return JpaEntityProperty with name <code> name </code>
     *@exception JpaEntityPropertyNotFoundException if such property does not exists.
     */
    public static<E,T> JpaEntityProperty<E,T>  findJpaPropertyByName(Class<E> entityClass, String name)
    {
        return JpaEntityProperty.<E,T>findByName(entityClass, name);
    }

}
