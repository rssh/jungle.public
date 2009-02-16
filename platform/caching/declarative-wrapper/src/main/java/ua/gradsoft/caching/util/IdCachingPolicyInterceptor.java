package ua.gradsoft.caching.util;

import java.lang.reflect.Method;
import ua.gradsoft.caching.CachingPolicyInterceptor;
import ua.gradsoft.caching.MethodCachingPolicy;

/**
 *Interceptor which do nothing.
 * @author rssh
 */
public class IdCachingPolicyInterceptor implements CachingPolicyInterceptor
{

    public MethodCachingPolicy intercept(MethodCachingPolicy prev, Object proxy, Method method, Object[] arguments) {
        return prev;
    }

}
