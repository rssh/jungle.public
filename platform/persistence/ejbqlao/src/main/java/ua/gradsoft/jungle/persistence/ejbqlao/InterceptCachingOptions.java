package ua.gradsoft.jungle.persistence.ejbqlao;

import java.lang.reflect.Method;
import java.util.Map;
import ua.gradsoft.caching.CacheAction;
import ua.gradsoft.caching.CachingPolicyInterceptor;
import ua.gradsoft.caching.MethodCachingPolicy;

/**
 *Caching policy interceptor for CRUD facade.
 *  (check nocache option)
 * @author rssh
 */
public class InterceptCachingOptions implements CachingPolicyInterceptor
{

    /**
     * check - if third argument is map with 'nocache'    
     */
    public MethodCachingPolicy intercept(MethodCachingPolicy prev, Object proxy, Method method, Object[] arguments) {
        if (arguments.length < 3) {
            return prev;
        }

        boolean nocache = false;
        Object candidat = arguments[2];
        if (candidat instanceof Map) {
            Map map = (Map)candidat;
            Object o = map.get("nocache");
            if (o!=null) {
                if (o instanceof Boolean) {
                    nocache = (Boolean)o;
                } else if (o instanceof Number) {
                    nocache = ((Number)o).intValue()!=0;
                } else {
                    // any value.
                    nocache = true;
                }
            }
        }

        MethodCachingPolicy retval = prev;
        if (nocache) {
            retval = new MethodCachingPolicy(prev);
            retval.setCacheAction(CacheAction.WITHOUT);
        }

        return retval;
    }

}
