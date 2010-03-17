/*
 * ClassSerializer.java
 *
 *
 *
 * @author rssh <Ruslan@Shevchenko.Kiev.UA>
 */

package org.jabsorb.serializer.impl;

import java.math.BigDecimal;
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
public class BigDecimalSerializer extends AbstractSerializer
{
    
    
  /**
   * Classes that this can serialise.
   */
  private static final Class[] _serializableClasses = new Class[] { BigDecimal.class };

  /**
   * Classes that this can serialise to.
   */
  private static final Class[] _JSONClasses = new Class[] { JSONObject.class };

  public boolean canSerialize(Class clazz, Class jsonClazz)
  {
    return clazz.equals(BigDecimal.class);
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
    BigDecimal bd;
    if (o instanceof BigDecimal)
    {
      bd = (BigDecimal)o;
    }
    else
    {
      throw new MarshallException("cannot marshall BigDecimal using class "
          + o.getClass());
    }
    JSONObject obj = new JSONObject();
    try
    {
      obj.put("javaClass", "java.math.BigDecimal");      
      obj.put("strvalue", bd.toString());
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
    if (!(o instanceof JSONObject)) {
      throw new UnmarshallException("not JSONObject ");
    }
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
    if (!(java_class.equals("java.math.BigDecimal")))
    {
      throw new UnmarshallException("not a BigDecimal");
    }
    state.setSerialized(o, ObjectMatch.OKAY);
    return ObjectMatch.OKAY;
  }
  
  public Object unmarshall(SerializerState state, Class clazz, Object o)
      throws UnmarshallException
  {
    Object returnValue=null;
    if (o instanceof JSONObject) {
      JSONObject jso = (JSONObject) o;
      String strvalue=null;    
      try {
        strvalue  = jso.getString("strvalue");          
      }catch(JSONException e){
          throw new UnmarshallException("Could not get strvalur in BigDecimal serialiser");
      }    
    
      try {
         returnValue = new BigDecimal(strvalue);
      }catch(NumberFormatException ex){
         throw new UnmarshallException("can't parse numer format " + strvalue ,ex);
      }
    } else if (o instanceof Integer) {
      returnValue=o;
    } else {
      throw new UnmarshallException("Could not unparse non-json object "+o.toString());
    }
    state.setSerialized(o, returnValue);
    return returnValue;
   }   
    
}
