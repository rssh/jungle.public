package ua.gradsoft.caching.util;

import java.io.Serializable;
import ua.gradsoft.caching.ValueBuilder;

/**
 *Builder, which return methodName as value.
 * @author rssh
 */
public class MethodName implements ValueBuilder {

  public Serializable build(Object object, String methodName, Object[] arguments)
  {
    return methodName;
  }



}
