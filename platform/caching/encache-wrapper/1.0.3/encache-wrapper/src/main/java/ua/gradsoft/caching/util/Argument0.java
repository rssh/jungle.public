
package ua.gradsoft.caching.util;

import java.io.Serializable;
import ua.gradsoft.caching.ValueBuilder;

/**
 *return 0-th argument.
* [note: argument numeration starts from 0]
 * @author rssh
 */
public class Argument0 implements ValueBuilder
{

  public Serializable build(Object object, String methodName, Object[] arguments)
  {
    return (Serializable)arguments[0];
  }


}
