package ua.gradsoft.jungle.spring;

import java.util.List;
import java.util.Map;
import org.jabsorb.JSONRPCBridge;
import org.jabsorb.serializer.Serializer;
import ua.gradsoft.jabsorb.phpjao.excptr.PhpJaoExceptionTransformer;
import ua.gradsoft.jabsorb.phpjao.excptr.StackTraceSerializer;


/**
 *  *Formal class for integration of JSON/RPC bridge with string bean
 **/
public class JSONRPCGlobalBridgeAccessBean
{

    public JSONRPCGlobalBridgeAccessBean()
    {
     JSONRPCBridge bridge = JSONRPCBridge.getGlobalBridge();
     PhpJaoExceptionTransformer tr = new PhpJaoExceptionTransformer();
     tr.setBridge(bridge);
     try {
       bridge.registerSerializer(new StackTraceSerializer());
     }catch(Exception ex){
       throw new IllegalStateException("Exception during registering StackTraceSerializer",ex);
     }
    }

    public void setRegisteredObjects(Map<String, Object> registeredObjects)
    {
      for(Map.Entry<String,Object> e: registeredObjects.entrySet()) {
          JSONRPCBridge.getGlobalBridge().registerObject(e.getKey(), e.getValue());
      }
    }

    public List<String> getCustomSerializerClasses()
    { return serializerClasses_; }

    public void setCustomSerializerClasses(List<String> serializerClasses)
    {
      try {
        for(String s:serializerClasses) {
          Class clazz = Class.forName(s);
          Object o = clazz.newInstance();
          JSONRPCBridge.getGlobalBridge().registerSerializer((Serializer)o);
        }
        serializerClasses_=serializerClasses;
      }catch(Exception ex){
          throw new IllegalStateException("Exception during registering serializers",ex);
      }
    }

    public boolean getFixupCircRefs()
    {
     return JSONRPCBridge.getGlobalBridge().getSerializer().getFixupCircRefs();
    }

    public void setFixupCircRefs(boolean value)
    {
     JSONRPCBridge.getGlobalBridge().getSerializer().setFixupCircRefs(value);
    }

    public boolean getFixupDuplicates()
    {
     return JSONRPCBridge.getGlobalBridge().getSerializer().getFixupDuplicates();
    }

    public void setFixupDuplicates(boolean value)
    {
     JSONRPCBridge.getGlobalBridge().getSerializer().setFixupDuplicates(value);
    }

    List<String> serializerClasses_;
    List<String> marshallClassHintClasses_;
}


