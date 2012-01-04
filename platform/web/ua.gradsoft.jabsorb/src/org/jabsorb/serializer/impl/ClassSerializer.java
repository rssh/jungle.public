/*
 * ClassSerializer.java
 *
 *
 * @author rssh <Ruslan@Shevchenko.Kiev.UA>
 */

package org.jabsorb.serializer.impl;

import org.jabsorb.serializer.AbstractSerializer;
import org.jabsorb.serializer.MarshallException;
import org.jabsorb.serializer.ObjectMatch;
import org.jabsorb.serializer.SerializerState;
import org.jabsorb.serializer.UnmarshallException;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Serialize instances of JavaClasses
 *(by names, of course).
 */
public class ClassSerializer extends AbstractSerializer
{
    
    
  /**
   * Classes that this can serialise.
   */
  private static final Class[] _serializableClasses = new Class[] { Class.class };

  /**
   * Classes that this can serialise to.
   */
  private static final Class[] _JSONClasses = new Class[] { JSONObject.class };

  public boolean canSerialize(Class clazz, Class jsonClazz)
  {
    return clazz.equals(Class.class);
  }

  public Class[] getJSONClasses()
  {
    return _JSONClasses;
  }

  public Class[] getSerializableClasses()
  {
    return _serializableClasses;
  }
  
  public Object marshall(SerializerState state, Object p, Object o)
      throws MarshallException
  {
    Class theClass;
    if (o instanceof Class)
    {
      theClass = (Class)o;
    }
    else
    {
      throw new MarshallException("cannot marshall Class using class "
          + o.getClass());
    }
    JSONObject obj = new JSONObject();
    try
    {
      obj.put("javaClass", "java.lang.Class");      
      obj.put("name", theClass.getName());
    }
    catch (JSONException e)
    {
      throw (MarshallException) new MarshallException(e.getMessage()).initCause(e);
    }
    return obj;
  }
  
  public ObjectMatch tryUnmarshall(SerializerState state, Class clazz, Object o)
      throws UnmarshallException
  {
    JSONObject jso = (JSONObject) o;
    String java_class;
    try
    {
      java_class = jso.getString("javaClass");
    }
    catch (JSONException e)
    {
      throw new UnmarshallException("no type hint");
    }
    if (java_class == null)
    {
      throw new UnmarshallException("no type hint");
    }
    if (!(java_class.equals("java.lang.Class")))
    {
      throw new UnmarshallException("not a Class");
    }
    state.setSerialized(o, ObjectMatch.OKAY);
    return ObjectMatch.OKAY;
  }
  
  public Object unmarshall(SerializerState state, Class clazz, Object o)
      throws UnmarshallException
  {
    JSONObject jso = (JSONObject) o;
    String name=null;    
    try {
      name = jso.getString("name");          
    }catch(JSONException e){
        throw new UnmarshallException("Could not get name in Class serialiser");
    }    
    Object returnValue = null;
    if (Class.class.equals(clazz))
    {
      try {  
         returnValue = Class.forName(name);
      }catch(ClassNotFoundException ex){
          throw new UnmarshallException("Can't find class "+name+" in Class serializer");
      }
    }
    if (returnValue == null)
    {
      throw new UnmarshallException("can't find class " + clazz);
    }
    state.setSerialized(o, returnValue);
    return returnValue;
   }   
    
}
