package ua.gradsoft.caching.util;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import ua.gradsoft.caching.CachingSystemException;
import ua.gradsoft.caching.ValueBuilder;

/**
 *AllArguments
 * @author rssh
 */
public class AllArguments implements ValueBuilder
{

  public Serializable build(Object object, String methodName, Object[] arguments)
  {
    ByteArrayOutputStream ba = new ByteArrayOutputStream();
    ObjectOutputStream out = null;
    try {
      out = new ObjectOutputStream(ba);
    //ArrayList<Serializable> ai = new ArrayList<Serializable>(arguments.length);
      for(int i=0; i<arguments.length; ++i){
        Object arg = arguments[i];
        //ai.add(i, (Serializable)arg);
        out.writeObject(arg);
      }
      out.flush();
      return new ValueByteBuffer(ba.toByteArray());
    } catch (IOException ex){
        throw new CachingSystemException("exception during creation of value buffer",ex);
    }
  }


}
