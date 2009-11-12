package ua.gradsoft.caching;

import java.io.Serializable;


public interface ValueBuilder
{

  public Serializable build(Object object, String methodName, Object[] arguments);

}
