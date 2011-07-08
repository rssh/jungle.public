package ua.gradsoft.caching;

import java.lang.reflect.Method;

/**
 *Interceptor for cache behavior
 * @author rssh
 */
public interface CachingPolicyInterceptor {

    MethodCachingPolicy intercept(MethodCachingPolicy prev,
                                  Object proxy,
                                  Method method,
                                  Object[] arguments);
    
}
