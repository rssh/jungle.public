package ua.gradsoft.caching;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *Instance of caching policy
 * @author rssh
 */
public class ClassCachingPolicy {

    public ClassCachingPolicy(ClassCachingPolicyDescription description)
    {
      description_=description;
      methodInstances_=new HashMap<Method,MethodCachingPolicy>();
    }

    public List<CachingRuleDescription> getRules()
    { return description_.getRules(); }

    public MethodCachingPolicy getMethodCachingInstance(Method method)
    {
       MethodCachingPolicy retval = methodInstances_.get(method);
       if (retval==null) {
           MethodCachingPolicyDescription mi = description_.getMethodCachingPolicyDescription(method);
           if (mi!=null) {
               retval = new MethodCachingPolicy(mi);
               methodInstances_.put(method, retval);
           }
       }
       return retval;
    }



    private ClassCachingPolicyDescription description_;
    private Map<Method,MethodCachingPolicy> methodInstances_;

}
