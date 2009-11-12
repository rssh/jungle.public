package ua.gradsoft.caching.util;

import java.io.Serializable;
import ua.gradsoft.caching.ValueBuilder;

/**
 *Builder, which return first argument (if one is serializable)
 * [note: argument numeration starts from 0]
 * @author rssh
 */
public class Argument1 implements ValueBuilder {

  public Serializable build(Object object, String methodName, Object[] arguments)
  {
    return (Serializable)arguments[1];
  }


}
