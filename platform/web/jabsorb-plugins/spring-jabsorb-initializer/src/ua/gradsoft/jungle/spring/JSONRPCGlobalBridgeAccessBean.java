package ua.gradsoft.jungle.spring;

import java.util.List;
import java.util.Map;
import org.jabsorb.JSONRPCBridge;
import org.jabsorb.serializer.ClassHintTranslator;
import org.jabsorb.serializer.Serializer;


/**
 *  *Formal class for integration of JSON/RPC bridge with string bean
 **/
public class JSONRPCGlobalBridgeAccessBean
{

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

    public List<String>  getMarshallClassHintTranslatorClasses()
    {
       return marshallClassHintClasses_;
    }

    public void setMarshallClassHintTranslatorClasses(List<String> translatorClasses)
    {
      try {
        for(String s:translatorClasses) {
          Class clazz = Class.forName(s);
          Object o = clazz.newInstance();
          if (o instanceof ClassHintTranslator) {
            JSONRPCBridge.getGlobalBridge().registerMarshallClassHintTranslator((ClassHintTranslator)o);
          } else {
             throw new IllegalArgumentException(o.getClass().getName()+" is not ClassHintTrabslator");
          }
        }
        marshallClassHintClasses_=translatorClasses;
      }catch(Exception ex){
          ex.printStackTrace();
          throw new IllegalStateException("Exception during registering serializers:"+ex.getMessage(),ex);
      }
    }

    public boolean getFixupCircRefs()
    {
     return JSONRPCBridge.getGlobalBridge().getSerializer().getFixupCircRefs();
    }

    public void setFixUpCircRefs(boolean value)
    {
     JSONRPCBridge.getGlobalBridge().getSerializer().setFixupCircRefs(value);
    }

    public boolean getFixupDuplicates()
    {
     return JSONRPCBridge.getGlobalBridge().getSerializer().getFixupDuplicates();
    }

    public void setFixUpDuplicates(boolean value)
    {
     JSONRPCBridge.getGlobalBridge().getSerializer().setFixupDuplicates(value);
    }

    List<String> serializerClasses_;
    List<String> marshallClassHintClasses_;
}


