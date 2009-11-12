package ua.gradsoft.jungle.persistence.jpaex.impl;

import java.lang.reflect.Field;
import javax.persistence.Column;
import javax.persistence.Id;
import ua.gradsoft.jungle.persistence.jpaex.JpaEntityProperty;

/**
 *
 * @author rssh
 */
public  class JpaEntityFieldProperty<E,T> extends JpaEntityProperty<E,T>
{

    public JpaEntityFieldProperty(Field field)
    {
      field_=field;
    }

    @Override
    public T getValue(E entity) {
      try {
        return (T)field_.get(entity);
      }catch(IllegalAccessException ex){
          throw new IllegalStateException("can't get value:",ex);
      }
    }

    @Override
    public void setValue(E entity, T value) {
      try {
        field_.set(entity, value);
      }catch(IllegalAccessException ex){
          throw new IllegalStateException("can't set value:",ex);
      }
    }

    @Override
    public String getName() {
        return field_.getName();
    }

    @Override
    public Class<E> getEntityClass() {
        return (Class<E>)field_.getDeclaringClass();
    }

    @Override
    public Class<T> getPropertyClass() {
        return (Class<T>)field_.getType();
    }



    @Override
    protected Column getColumnAnnotation() {
        return field_.getAnnotation(Column.class);
    }


    @Override
    public boolean isId() {
        return field_.getAnnotation(Id.class)!=null;
    }



    private Field  field_;
}
