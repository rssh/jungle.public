
package ua.gradsoft.jungle.persistence.jpaex.impl;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import ua.gradsoft.jungle.persistence.jpaex.JPASystemException;
import ua.gradsoft.jungle.persistence.jpaex.JpaEntityProperty;
import ua.gradsoft.jungle.persistence.jpaex.JpaHelper;

/**
 *Clone (and detach) JPA entity in collections or classes, leave all other unchanged.
 *For using this class, our JPA Entities must meet few conditions:
 *<UL>
 *  <li> all entitis must have public default constructores </li>
 *  <li> JPA properties must be defined throught public methods </li>
 *  <li> objects, supplied to method must be only JPAEntities or colections of
 *   JPAEntities. I. e. if we set JPAEntity inot some other bean and then pass this
 *   bean to JPAEntityCloner, than our entity will not be found by cloner. </li>
 *</UL>
 * @author rssh
 */
public class JpaEntityCloner {


    public static Object unjpaObject(Class objClass, Object o, IdentityHashMap<Object,Object> visited)
    {
      if (o==null) return null;  
      if (visited == null) {
          visited = new IdentityHashMap<Object,Object>();
      }        
      Object hashed = visited.get(o);
      if (hashed!=null) {
          return hashed;
      }
      if (o instanceof Map) {
          return unjpaMap(objClass,(Map)o, visited);
      }
      if (o instanceof Collection) {
         return unjpaCollection(objClass, (Collection)o, visited);
      }
      
      Class entityClass = JpaHelper.findSameOrSuperJpaEntity(o.getClass());
      if (entityClass!=null) {
          return unjpaEntity(entityClass, o, visited);
      }
      
      // check for array.
      if (o instanceof Object[]) {
          visited.put(o, o);
          Object[] ao = (Object[])o;
          for(int i=0; i<ao.length; ++i) {
              ao[i]=unjpaObject(Object.class, ao[i], visited);
          }
      }
      
      // not collection and not Entity -- return unchanged.
      visited.put(o, o);
      return o;
    }
    
    public static Object unjpaEntity(Class<?> entityClass,Object entity, IdentityHashMap<Object,Object> visited)
    {
      if ((entityClass.getModifiers() & Modifier.ABSTRACT)!=0) {
        // get entityClass as one of concrete superclass of entity
        entityClass=JpaHelper.findSameOrSuperJpaEntity(entity.getClass());
        if ((entityClass.getModifiers() & Modifier.ABSTRACT)!=0) {
            // it means, that we have no non-abstract non-generate property,
            entityClass = entity.getClass();
        }
      }
      try {
        Constructor cn = entityClass.getConstructor(emptyClassArray);
        Object retval = cn.newInstance(emptyObjectArray);
        visited.put(entity, retval);
        for(JpaEntityProperty p: JpaHelper.getAllJpaProperties(entityClass,true)) {
            Object value = p.getValue(entity);
            p.setValue(retval, unjpaObject(p.getPropertyClass(), value, visited));
        }
        return retval;
      }catch(NoSuchMethodException ex){
          throw new IllegalArgumentException("Class "+entityClass.getName()+" must have default constructor");
      }catch(InstantiationException ex){
          throw new JPASystemException("Can't instantiate class "+entityClass.getName(), ex);
      }catch(IllegalAccessException ex){
          throw new JPASystemException("Can't instantiate class "+entityClass.getName(), ex);
      }catch(InvocationTargetException ex){
          throw new JPASystemException("Can't instantiate class "+entityClass.getName(), ex);
      }
    }
    
    public static Object unjpaCollection(Class collectionClass, Collection<Object> collection, IdentityHashMap<Object,Object> visited)
    {
      if (!collectionClass.isInterface()) {
          // try to instantiate collection.
          Collection retval = null;
          try {
            Constructor cn = collectionClass.getConstructor(emptyClassArray);
            retval=(Collection)cn.newInstance();
          }catch(Exception ex){
              /* do nothing */
          }
          if (retval!=null) {
              visited.put(collection, retval);
              for(Object e: collection) {
                  retval.add(unjpaObject(Object.class,e,visited));
              }
              return retval;
          }
      }  
      // at first - check for elementary collection types.                
          if (collectionClass.isAssignableFrom(List.class)) {
              ArrayList<Object> retval = new ArrayList<Object>(collection.size());
              visited.put(collection, retval);
              for(Object e: collection) {
                  retval.add(unjpaObject(Object.class,e,visited));
              }
              return retval;
          }else if(collectionClass.isAssignableFrom(Set.class)) {
              HashSet<Object> retval = new HashSet<Object>();
              visited.put(collection, retval);
              for(Object e:collection) {
                  retval.add(unjpaObject(Object.class,e,visited));
              }
              return retval;
          }else{
             throw new IllegalStateException("Can't find stasndard collection analog for class "+collectionClass.getName());
          }
        
    }
    
    public static Object unjpaMap(Class mapClass, Map<Object,Object> map,IdentityHashMap<Object,Object> visited)
    {
      HashMap<Object,Object> retval = new HashMap<Object,Object>();
      visited.put(map, retval);
      for(Map.Entry<Object,Object> e : map.entrySet()) {
          retval.put(e.getKey(), unjpaObject(Object.class,e,visited));
      }
      return retval;
    }

    private static final Class[] emptyClassArray = new Class[0];
    private static final Object[] emptyObjectArray = new Object[0];
}
