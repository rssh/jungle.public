package ua.gradsoft.caching.util;

import java.io.Serializable;
import ua.gradsoft.caching.CachingSystemException;
import ua.gradsoft.caching.ValueBuilder;

/**
 *Empty value builder
 * @author rssh
 */
public class EmptyValueBuilder implements ValueBuilder
{

  public Serializable build(Object object, String methodName, Object[] arguments)
  {
    throw new CachingSystemException("EmptyValueBuilder: one of keyBuilder or valurBuilder annotations is not set");
  }


}
