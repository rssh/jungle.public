
package ua.gradsoft.jungle.persistence.jpaex.impl;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import javax.persistence.Column;
import javax.persistence.Id;
import ua.gradsoft.jungle.persistence.jpaex.JpaEntityProperty;

/**
 *JpaEntity, formed by pair of getters and setters.
 * @author rssh
 */
public class JpaEntityMethodPairProperty<T,E> extends JpaEntityProperty<T,E>
{

    public JpaEntityMethodPairProperty(String name,
                                       Method getter,
                                       Method setter)
    {    
      name_=name;
      getter_=getter;
      setter_=setter;
    }

    public String getName()
    { return name_; }

    public T  getValue(E entity)
    {
      try {
        return (T)getter_.invoke(entity, new Object[0]);
      }catch(IllegalAccessException ex){
          throw new IllegalStateException("can't call getter",ex);
      }catch(InvocationTargetException ex){
          throw new IllegalStateException("can't call getter",ex);
      }
    }

    public void  setValue(E entity, T value)
    {
      try{
        setter_.invoke(entity, value);
      }catch(IllegalAccessException ex){
          throw new IllegalStateException("can't call getter",ex);
      }catch(InvocationTargetException ex){
          throw new IllegalStateException("can't call getter",ex);
      }
    }

    public boolean isId()
    {
        return getter_.getAnnotation(Id.class)!=null;
    }

    public Class<E> getEntityClass()
    {
        return (Class<E>)getter_.getDeclaringClass();
    }

    
    public Class<T> getPropertyClass()
    {
        return (Class<T>)getter_.getReturnType();
    }

    protected Column getColumnAnnotation()
    {
        return getter_.getAnnotation(Column.class);
    }

    private Method  getter_;
    private Method  setter_;
    private String  name_;

}
