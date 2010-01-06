package org.jabsorb.serializer.impl;

import java.math.BigDecimal;
import java.util.List;
import java.util.WeakHashMap;
import org.jabsorb.JSONSerializer;
import org.jabsorb.serializer.MarshallException;
import org.jabsorb.serializer.ObjectMatch;
import org.jabsorb.serializer.SerializerState;
import org.jabsorb.serializer.UnmarshallException;
import org.json.JSONException;
import org.json.JSONObject;
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
        return super.canSerialize(clazz, jsonClazz) 
                && getHashedPojoClass(clazz)!=null
                && !Number.class.isAssignableFrom(clazz);
    }

    @Override
    public Object marshall(SerializerState state, Object p, Object o) throws MarshallException {
        Class pojoClass = getHashedPojoClass(o.getClass());
        if (pojoClass==null) {
           return super.marshall(state, p, o);
        }else {
          // Object pojoObject = makePlainPojo(pojoClass,o);
          // return super.marshall(state, null, pojoObject);
          List<JpaEntityProperty> props = JpaHelper.getAllJpaProperties(pojoClass, true);
          JSONObject val = new JSONObject();
          if (ser.getMarshallClassHints()) {
            try {
              val.put("javaClass", pojoClass.getName());
            }catch(JSONException ex){
                throw new MarshallException("JSONException: "+ex.getMessage(),ex);
            }
          }
          for(JpaEntityProperty property: props) {
             Object pv = property.getValue(o);
             if (pv!=null || ser.getMarshallNullAttributes()) {
                 Object json = ser.marshall(state, o, pv, property.getName());
                 if (JSONSerializer.CIRC_REF_OR_DUPLICATE!=json) {
                     try {
                        val.put(property.getName(),json);
                     } catch (JSONException ex) {
                        throw new MarshallException("JSONException: "+ex.getMessage(),ex);
                     }
                 }
             }
          }
          return val;
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

