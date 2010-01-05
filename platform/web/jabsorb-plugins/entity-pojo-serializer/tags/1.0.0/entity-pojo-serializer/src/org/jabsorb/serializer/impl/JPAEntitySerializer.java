package org.jabsorb.serializer.impl;

import java.lang.annotation.Annotation;
import java.util.List;
import java.util.WeakHashMap;
import org.jabsorb.serializer.MarshallException;
import org.jabsorb.serializer.ObjectMatch;
import org.jabsorb.serializer.SerializerState;
import org.jabsorb.serializer.UnmarshallException;
import ua.gradsoft.jungle.persistence.jpaex.JpaEntityProperty;
import ua.gradsoft.jungle.persistence.jpaex.JpaHelper;


/**
 * Before marshalling, transform jpa-enhanched classes to plain POJO-s,
 * and store lazy-loaded variables.
 * @author rssh
 */
public class JPAEntitySerializer extends BeanSerializer
{

    @Override
    public boolean canSerialize(Class clazz, Class jsonClazz) {
        return super.canSerialize(clazz, jsonClazz) && getHashedPojoClass(clazz)!=null;
    }

    @Override
    public Object marshall(SerializerState state, Object p, Object o) throws MarshallException {
        Class pojoClass = getHashedPojoClass(o.getClass());
        if (pojoClass==null) {
           return super.marshall(state, p, o);
        }else if (pojoClass.equals(o.getClass())) {
           return super.marshall(state, p, o);
        }else{
            // now copy all properties to pojoClass
            Object pojoObject = null;
            try {
             pojoObject = pojoClass.newInstance();
            }catch(InstantiationException ex){
                throw new MarshallException("Can't instantiate "+pojoClass.getName()+":"+ex.getMessage(),ex);
            }catch(IllegalAccessException ex){
                throw new MarshallException("Can't instantiate "+pojoClass.getName()+":"+ex.getMessage(),ex);
            }
            List<JpaEntityProperty> props = JpaHelper.getAllJpaProperties(pojoClass, true);
            for(JpaEntityProperty property: props) {
                property.setValue(pojoObject, property.getValue(o));
            }
            return super.marshall(state, p, pojoObject);

        }
    }

    @Override
    public ObjectMatch tryUnmarshall(SerializerState state, Class clazz, Object o) throws UnmarshallException {
        return super.tryUnmarshall(state, clazz, o);
    }

    @Override
    public Object unmarshall(SerializerState state, Class clazz, Object o) throws UnmarshallException {
        return super.unmarshall(state, clazz, o);
    }


  private Class getHashedPojoClass(Class clazz)
  {
    Class rClazz = _hash.get(clazz);
    if (rClazz==null) {
        rClazz = JpaHelper.findSameOrSuperJpaEntity(clazz);
        if (rClazz==null) {
            synchronized(JPAEntitySerializer.class) {
              _hash.put(clazz,Void.class);
            }
        } else {
            synchronized(JPAEntitySerializer.class) {
              _hash.put(clazz, rClazz);
            }
        }
    } else {
        if (rClazz.equals(Void.class)) {
            return null;
        }
    }
    return rClazz;
  }



  private static WeakHashMap<Class,Class> _hash = new WeakHashMap<Class,Class>();


}

