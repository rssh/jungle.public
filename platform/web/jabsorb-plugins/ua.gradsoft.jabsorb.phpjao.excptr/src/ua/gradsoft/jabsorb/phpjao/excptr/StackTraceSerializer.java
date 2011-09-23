
package ua.gradsoft.jabsorb.phpjao.excptr;

import org.jabsorb.JSONSerializer;
import org.jabsorb.serializer.MarshallException;
import org.jabsorb.serializer.ObjectMatch;
import org.jabsorb.serializer.Serializer;
import org.jabsorb.serializer.SerializerState;
import org.jabsorb.serializer.UnmarshallException;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 *Serializer of StackTrace
 * @author rssh
 */
public class StackTraceSerializer implements Serializer
{

    public boolean canSerialize(Class type, Class jsonType) {
        return type.equals(StackTraceElement[].class);
    }

    public Class[] getJSONClasses() {
         return JSON_CLASSES;
    }

    public Class[] getSerializableClasses() {
        return CLASSES;
    }

    public Object marshall(SerializerState ss, Object o, Object o1) throws MarshallException {
        if (o instanceof StackTraceElement[]) {
            StackTraceElement elements[] = (StackTraceElement[])o;
            JSONObject retval = new JSONObject();
            try {
              retval.put("javaClass", StackTraceElement[].class.getName());
              JSONArray array = new JSONArray();
              for(StackTraceElement  e: elements) {
                JSONObject eo = new JSONObject();
                eo.put("className", e.getClassName());
                eo.put("methodName", e.getMethodName());
                eo.put("fileName", e.getFileName());
                eo.put("lineNumber", e.getLineNumber());
                array.put(eo);
              }
              retval.put("elements", array);
            } catch (JSONException ex) {
                throw new MarshallException("exception during marshalling",ex);
            }
            return retval;
        } else {
            throw new MarshallException("must be StackTraceElement[]");
        }
    }

    public void setOwner(JSONSerializer jsons) {
       // not used,
    }

    public ObjectMatch tryUnmarshall(SerializerState ss, Class classToUnmarshall, Object o) throws UnmarshallException {
      try {
        if (o instanceof JSONObject) {
          JSONObject jo = (JSONObject)o;
          if (classToUnmarshall!=null) {
              if (!classToUnmarshall.getName().equals(StackTraceElement[].class.getName())) {
                  throw new UnmarshallException("mismatch of classToUnmarshall");
              }
          }
          Object sclass = jo.get("javaClass");
          if (sclass==null) {
              throw new UnmarshallException("java hint is absent");
          }else{
              if (!sclass.equals(StackTraceElement[].class.getName())) {
                  throw new UnmarshallException("class hint mismatch, must be:"+
                                                 StackTraceElement[].class.getName()+
                                                 "have "+sclass);
              }
          }
          Object arr = jo.get("elements");
          if (arr==null) {
              throw new UnmarshallException("elements component is absent");
          }
          return ObjectMatch.OKAY;
        } else {
          throw new UnmarshallException("JObject required");
        }
       } catch (JSONException ex) {
           throw new UnmarshallException("exception during canUnmarshall",ex);
       }
    }

    public Object unmarshall(SerializerState ss, Class type, Object o) throws UnmarshallException {
          if (!(o instanceof JSONObject)) {
              throw new UnmarshallException("JSONObject required instead "+o.toString());
          }
          try {
            JSONObject jo = (JSONObject)o;
            Object oarr = jo.get("elements");
            if (oarr==null) {
              throw new UnmarshallException("elements compoinent is absent");
            }
            if (!(oarr instanceof JSONArray)) {
              throw new UnmarshallException("elements must be array");
            }
            JSONArray arr = (JSONArray)oarr;
            StackTraceElement retval[] = new StackTraceElement[arr.length()];
            for(int i=0; i<arr.length(); ++i) {
              JSONObject eo = arr.getJSONObject(i);
              retval[i] = new StackTraceElement(eo.getString("className"),
                                                eo.getString("methodName"),
                                                eo.getString("fileName"),
                                                eo.getInt("lineNumber")
                                                );
            }
            return retval;
        }catch(JSONException ex){
            throw new UnmarshallException("exception in unmarshall",ex);
        }
    }

    private static final Class<?> JSON_CLASSES[] = { JSONObject.class };
    private static final Class<?> CLASSES[] = { StackTraceElement[].class };



}
