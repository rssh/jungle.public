package ua.gradsoft.jungle.persistence.jpaex;


import java.lang.annotation.Annotation;
import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.EmbeddedId;
import javax.persistence.Enumerated;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.Lob;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Temporal;
import ua.gradsoft.jungle.persistence.jpaex.impl.JpaEntityComplexIdClassProperty;
import ua.gradsoft.jungle.persistence.jpaex.impl.JpaEntityFieldProperty;
import ua.gradsoft.jungle.persistence.jpaex.impl.JpaEntityMethodPairProperty;

/**
 * Helper class for JPA Entity, which incapsulate
 * access to property value.
 */
public abstract class JpaEntityProperty<E,T>
{

    /**
     * get Name of JPA property.
     * @return
     */
    public abstract String getName();

    public String getColumnName()
    {
      Column cn = getColumnAnnotation();
      if (cn!=null) {
          if (cn.name()!=null) {
              return cn.name();
          } else {
              return getName();
          }
      } else {
          return getName();
      }
    }

    public abstract T getValue(E entity);

    public abstract void  setValue(E entity, T value);
    
    public abstract boolean isId();
    
    public abstract Class<E>  getEntityClass();
    
    public abstract Class<T>  getPropertyClass();

    public JpaCollectionType  getJpaCollectionType()
    {
       Class<T> propertyClass = getPropertyClass();
       if (Collection.class.isAssignableFrom(propertyClass)) {
         if (List.class.isAssignableFrom(propertyClass)) {
            return JpaCollectionType.LIST;
         } else if (Set.class.isAssignableFrom(propertyClass)) {
            return JpaCollectionType.SET;
         } else {
            return JpaCollectionType.COLLECTION;
         }
       } else if (Map.class.isAssignableFrom(propertyClass)) {
           return JpaCollectionType.MAP;
       } else {
           return JpaCollectionType.NONE;
       }
    }

    /**
     *Find JPA property with name <code> name </code> in class <code> entityClass </code>
     * @param entityClass - entity, where search properties.
     * @param name - name of property to find.
     * @return JpaEntityProperty with name <code> name </code> if one exists,
     *  otherwise - trow JpaEntityPropertyNotFoundException
     *@exception JpaEntityPropertyNotFoundException
     */
    public static<EC,TC> JpaEntityProperty<EC,TC> findByName(Class<EC> entityClass, String name)
    {
        // at first, generate method-s name and
        String getterName = generateGetterName(name);
        Method getter;
        try {
          getter = entityClass.getMethod(getterName, new Class[0]);
        }catch(NoSuchMethodException ex){
            getter=null;
        }
        if (getter!=null) {
            Class[] setterParams = new Class[1];
            setterParams[0] = getter.getReturnType();
            Method setter;
            try {
             setter = entityClass.getMethod(generateSetterName(name), setterParams);
            } catch(NoSuchMethodException ex){
                throw new IllegalArgumentException("getter without setter for propeety "+name);
            }
            return new JpaEntityMethodPairProperty<EC,TC>(name,getter,setter);
        }


        Field field;
        try {
          field = entityClass.getField(name);
        }catch(NoSuchFieldException ex){
            field=null;
        }
        if (field!=null) {
            return new JpaEntityFieldProperty<EC,TC>(field);
        }
        
        // property not found
        throw new JpaEntityPropertyNotFoundException(entityClass,name);
    }

    /**
     * Find in entityClass property, which
     * @param entityClass
     * @return
     */
    public static<EC,TC> JpaEntityProperty<EC,TC> findByColumnName(Class<EC> entityClass, String columnName)
    {

        // iterate over all getters.
        Method[] methods = entityClass.getMethods();
        for(Method method: methods) {
            String methodName = method.getName();
            if (methodName.startsWith("get")) {
                Column cn = method.getAnnotation(Column.class);
                if (cn!=null) {
                    if (cn.name()!=null) {
                        if (cn.name().equalsIgnoreCase(columnName)) {
                            // found, check setter
                            String propertyName = getNameFromGetter(method.getName());
                            Method setter = null;
                            try{
                              setter=entityClass.getMethod(generateSetterName(propertyName),
                                                           method.getReturnType());
                            }catch(NoSuchMethodException ex){
                               throw new IllegalStateException("getter woithout setter for property "+propertyName,ex);
                            }
                            return new JpaEntityMethodPairProperty(propertyName,method,setter);
                        }else{
                            continue;
                        }
                    }
                }
                String propertyName = getNameFromGetter(method.getName());
                if (propertyName.equalsIgnoreCase(columnName)) {
                    Method setter;
                    try {
                       setter = entityClass.getMethod(generateSetterName(propertyName),
                                                      method.getReturnType());
                    }catch(NoSuchMethodException ex){
                       throw new IllegalStateException("getter woithout setter for property "+propertyName);
                    }
                    return new JpaEntityMethodPairProperty<EC,TC>(propertyName,method,setter);
                }
            }
        } // for

        //if we still here - search in fiel-based properties.
        Field[] fields = entityClass.getFields();
        for(int i=0; i<fields.length; ++i) {
            Column cn = fields[i].getAnnotation(Column.class);
            if (cn!=null) {
                if (cn.name()!=null) {
                    if (cn.name().equalsIgnoreCase(columnName)) {
                        return new JpaEntityFieldProperty(fields[i]);
                    }else{
                        continue;
                    }
                }else{
                    if (fields[i].getName().equalsIgnoreCase(columnName)) {
                        return new JpaEntityFieldProperty<EC,TC>(fields[i]);
                    }
                }
            }else{
              if (fields[i].getName().equalsIgnoreCase(columnName)) {
                 return new JpaEntityFieldProperty(fields[i]);
              }
            }
        }

        //
        throw new JpaEntityPropertyNotFoundException();
    }


    /**
     * Find id property. If we have complex id
     * @param entityClass
     * @return id property, with class.
     */
    public static<EC,TC> JpaEntityProperty<EC,TC> findId(Class<EC> entityClass)
    {
      Annotation idClass = entityClass.getAnnotation(IdClass.class);
      if (idClass!=null) {
          return new JpaEntityComplexIdClassProperty(entityClass,((IdClass)idClass).value());
      }
      Method[] methods = entityClass.getDeclaredMethods();
      for(Method method:methods) {
          if (method.getName().startsWith("get")) {
              Id idAnnotation = method.getAnnotation(Id.class);
              if (idAnnotation!=null) {
                  Method setter;
                  try {
                      setter = entityClass.getMethod("set"+method.getName().substring(3),
                                                     method.getReturnType());
                  }catch(NoSuchMethodException ex){
                      throw new IllegalStateException("getter without setter for "+method.getName());
                  }
                  String propertyName = getNameFromGetter(method.getName());
                  return new JpaEntityMethodPairProperty(propertyName,method,setter);
              }
          }
      }

      //ig we here, than we found nothing in methods. let's search in field.
      Field[] fields = entityClass.getFields();
      for(Field field:fields) {
          Id idAnnotation = field.getAnnotation(Id.class);
          if (idAnnotation!=null) {
              return new JpaEntityFieldProperty(field);
          }
      }

      throw new JpaEntityPropertyNotFoundException();
    }

    public static List<JpaEntityProperty>  getAllPropertiesForEntity(Class entityClass)
    {
       List<JpaEntityProperty> retval = new LinkedList<JpaEntityProperty>();
       for(Method method: entityClass.getMethods()) {
           if (method.getName().startsWith("get")) {
               if (method.getParameterTypes().length==0 && checkJpaAnnotations(method)) {
                   //ok, cand
                   try {
                       Method setter = entityClass.getMethod("set"+method.getName().substring(3),
                                                             method.getReturnType());
                       String propertyName = getNameFromGetter(method.getName());
                       retval.add(new JpaEntityMethodPairProperty(propertyName,method,setter));
                   }catch(NoSuchMethodException ex){
                       // ex.ignore();
                       ;
                   }
               }
           }
       }

       for(Field field: entityClass.getFields()) {
           if (checkJpaAnnotations(field)) {
               retval.add(new JpaEntityFieldProperty(field));
           }
       }

       return retval;
    }
    
    protected abstract Column getColumnAnnotation();

    protected static String generateGetterName(String name)
    {
      return "get"+Character.toUpperCase(name.charAt(0))+name.substring(1);
    }

    protected static String generateSetterName(String name)
    {
      return "set"+Character.toUpperCase(name.charAt(0))+name.substring(1);
    }

    protected static String  getNameFromGetter(String name)
    {
       if (name.length()<=4) {
           throw new IllegalArgumentException("name "+name+" can't be a name of getter");
       }
       return Character.toLowerCase(name.charAt(3))+name.substring(4);
    }

    private static boolean checkJpaAnnotations(AccessibleObject m)
    {             
      return m.isAnnotationPresent(Basic.class) ||
             m.isAnnotationPresent(Column.class) ||
             m.isAnnotationPresent(Id.class) ||
             m.isAnnotationPresent(Temporal.class) ||
             m.isAnnotationPresent(Embedded.class) ||
             m.isAnnotationPresent(Lob.class) ||
             m.isAnnotationPresent(Enumerated.class) ||
             m.isAnnotationPresent(EmbeddedId.class) ||
             m.isAnnotationPresent(OneToMany.class) ||
             m.isAnnotationPresent(OneToOne.class) ||
             m.isAnnotationPresent(ManyToMany.class) ||
             m.isAnnotationPresent(ManyToOne.class)
             ;
    }

}
