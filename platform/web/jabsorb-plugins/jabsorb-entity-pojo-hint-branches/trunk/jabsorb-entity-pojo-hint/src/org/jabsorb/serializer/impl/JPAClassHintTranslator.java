package org.jabsorb.serializer.impl;

import java.lang.annotation.Annotation;
import java.util.WeakHashMap;
import org.jabsorb.serializer.ClassHintTranslator;

/**
 * Substitute pojo-enhachend classes to real POJO-s.
 */
public class JPAClassHintTranslator implements ClassHintTranslator
{

  public Class translate(Class clazz)
  {
    return getHashedPojoClass(clazz);
  }

  private Class getHashedPojoClass(Class clazz)
  {
    Class rClazz = _hash.get(clazz);
    if (rClazz==null) {
        rClazz = getPojoClass(clazz);
        if (rClazz==null) {
            synchronized(JPAClassHintTranslator.class) {
              _hash.put(clazz,Void.class);
            }
        } else {
            synchronized(JPAClassHintTranslator.class) {
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

  private Class getPojoClass(Class clazz) 
  {
    while(clazz!=null) {
      Annotation[] annotations = clazz.getDeclaredAnnotations();
      for(int i=0; i<annotations.length; ++i) {
        if (annotations[i] instanceof javax.persistence.Entity) {
         return clazz;
        }
      }
      clazz = clazz.getSuperclass();
      if (clazz == null) {
         break;
      }
      if (clazz.isEnum()) {
         break;
      }
      if (clazz.equals(Object.class)) {
         break;
      }
    }
    return null;
  }


  private static WeakHashMap<Class,Class> _hash = new WeakHashMap<Class,Class>();

}
