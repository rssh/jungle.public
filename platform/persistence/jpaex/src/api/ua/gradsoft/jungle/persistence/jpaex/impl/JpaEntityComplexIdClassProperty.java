package ua.gradsoft.jungle.persistence.jpaex.impl;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.LinkedList;
import java.util.List;
import javax.persistence.Column;
import javax.persistence.Id;
import ua.gradsoft.jungle.persistence.jpaex.JpaEntityProperty;
import ua.gradsoft.jungle.persistence.jpaex.JpaEntityPropertyNotFoundException;

/**
 *Property for entities with idClass annotations:
 * type of property is IdClass,
 * @author rssh
 */
public class JpaEntityComplexIdClassProperty<T,E> extends JpaEntityProperty<T,E>
{

    public JpaEntityComplexIdClassProperty(Class<E> entityClass, Class<T> idClass)
    {
       entityClass_=entityClass;
       idClass_=idClass;
       buildIdProperties();
    }

    /**
     * @return null
     */
    @Override
    protected Column getColumnAnnotation() {
        return null;
    }

    @Override
    public Class<E> getEntityClass() {
        return entityClass_;
    }

    /**
     * IdClass id entity have no name, so return null.
     * @return null
     */
    @Override
    public String getName() {
        return null;
    }

    @Override
    public Class<T> getPropertyClass() {
        return idClass_;
    }

    @Override
    public T getValue(E entity) {
      try {
        Object retval = idClass_.newInstance();
        for(JpaEntityProperty p: idProperties_) {
            setIdClassProperty(retval,p,p.getValue(entity));
        }
        return (T)retval;
      }catch(InstantiationException ex){
          throw new IllegalStateException("Can't instantiate id object",ex);
      }catch(IllegalAccessException ex){
          throw new IllegalStateException("Can't instantiate id object",ex);
      }
    }

    @Override
    public void setValue(E entity, T value) {
        for(JpaEntityProperty p: idProperties_) {
            Object v = getIdClassProperty(value,p);
            p.setValue(entity, v);
        }
    }



    @Override
    public boolean isId() {
        return true;
    }


    private void buildIdProperties()
    {
        idProperties_ = new LinkedList<JpaEntityProperty>();
        for(Method method: entityClass_.getMethods()) {
            if (method.getName().startsWith("get") && method.getAnnotation(Id.class)!=null) {
                String propertyName = JpaEntityProperty.getNameFromGetter(method.getName());
                try {
                    Method setter = entityClass_.getMethod("set"+method.getName().substring(3),
                                                           method.getReturnType());
                    idProperties_.add(new JpaEntityMethodPairProperty(propertyName, method, setter));
                }catch(NoSuchMethodException ex){
                    throw new IllegalArgumentException("getter without setter for "+method.getName());
                }
            }
        }

        for(Field field: entityClass_.getFields()) {
           if (field.isAnnotationPresent(Id.class)) {
               idProperties_.add(new JpaEntityFieldProperty(field));
           }
        }
    }

    private static void setIdClassProperty(Object o, JpaEntityProperty property, Object propertyValue)
    {
        boolean tryMethod=false;
        try {
            Field f = o.getClass().getField(property.getName());
            f.set(o, propertyValue);
        }catch(NoSuchFieldException ex){
             tryMethod=true;
        }catch(IllegalAccessException ex){
            throw new IllegalArgumentException("IllegalAccess for property "+property.getName(),ex);
        }

        if (!tryMethod) {
            return;
        }

        String setterName = generateSetterName(property.getName());
        boolean propertySet=false;
        try {
            Method method = o.getClass().getMethod(setterName, property.getPropertyClass());
            method.invoke(o, propertyValue);
            propertySet=true;
        } catch (NoSuchMethodException ex){
            throw new JpaEntityPropertyNotFoundException();
        } catch (IllegalAccessException ex){
            throw new IllegalArgumentException("IllegalAccess for property "+property.getName(),ex);
        } catch (InvocationTargetException ex){
            throw new IllegalStateException("exception during setting property "+property.getName(),ex);
        }

        if (!propertySet) {
            throw new JpaEntityPropertyNotFoundException(o.getClass(),property.getName());
        }
    }

    private static Object getIdClassProperty(Object o, JpaEntityProperty property)
    {
      try {
          Field field = o.getClass().getField(property.getName());
          return field.get(o);
      } catch (NoSuchFieldException ex){
          // ignore
          ;
      } catch (IllegalAccessException ex){
           throw new IllegalArgumentException("IllegalAccess for property "+property.getName(),ex);
      }

      try {
          Method method = o.getClass().getMethod(generateGetterName(property.getName()),
                                                 new Class[0]);
          return method.invoke(o);
      } catch (NoSuchMethodException ex){
          throw new JpaEntityPropertyNotFoundException(o.getClass(),property.getName());
      } catch (IllegalAccessException ex){
          throw new IllegalArgumentException("IllegalAccess for property "+property.getName(),ex);
      } catch(InvocationTargetException ex){
          throw new IllegalArgumentException("Exception during setting property "+property.getName(),ex);
      }

    }

    private Class<E> entityClass_;
    private Class<T> idClass_;

    private List<JpaEntityProperty> idProperties_;


}
