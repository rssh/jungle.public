
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
     *@param entityClass class, for which we search entity.
     *@param searchInSupers -- if true, then get all JPA propertries including propertires of all superclasses
     **/
    public static<E> List<JpaEntityProperty<E,Object>> getAllJpaProperties(Class<E> entityClass, boolean searchInSupers)
    {
      return JpaEntityProperty.<E>getAllPropertiesForEntity(entityClass,searchInSupers);
    }


    /**
     * get Jpa property, with name <code> name </code>
     *@param entityClass - class were w e search propertries
     *@param name - name of property to search
     *@param searchInSupers - if set, search throught inheritance layers.
     *@return JpaEntityProperty with name <code> name </code>
     *@exception JpaEntityPropertyNotFoundException if such property does not exists.
     */
    public static<E,T> JpaEntityProperty<E,T>  findJpaPropertyByName(Class<E> entityClass, String name, boolean searchInSupers)
    {
        return JpaEntityProperty.<E,T>findByName(entityClass, name, searchInSupers);
    }

    /**
     * get Jpa property, with name <code> name </code>
     *@return JpaEntityProperty with name <code> name </code>
     *@exception JpaEntityPropertyNotFoundException if such property does not exists.
     */
    public static<E,T> JpaEntityProperty<E,T>  findIdJpaProperty(Class<E> entityClass, boolean searchInSupers)
    {
        return JpaEntityProperty.<E,T>findId(entityClass, searchInSupers);
    }


}
