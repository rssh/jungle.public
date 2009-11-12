
package ua.gradsoft.caching;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

/**
 *Federated class cashing policy - to use at first - config file (if exists), than annotations.
 * @author rssh
 */
public class FederatedClassCachingPolicyDescription implements ClassCachingPolicyDescription
{


    public FederatedClassCachingPolicyDescription(
            ClassCachingPolicyDescription first,
            ClassCachingPolicyDescription second
            )
    {
        first_=first;
      second_=second;
    }

    public MethodCachingPolicyDescription getMethodCachingPolicyDescription(Method m) {
        MethodCachingPolicyDescription retval = null;
        if (first_!=null) {
            retval = first_.getMethodCachingPolicyDescription(m);
        }
        if (retval==null) {
          if (second_!=null) {
            retval = second_.getMethodCachingPolicyDescription(m);
          }
        }
        return retval;
    }

    public List<CachingRuleDescription> getRules() {
        List<CachingRuleDescription> firstRules = null;
        List<CachingRuleDescription> secondRules = null;
        if (first_!=null) {
           firstRules = first_.getRules();
        }
        if (second_!=null) {
           secondRules = second_.getRules();
        }
        if (firstRules==null || firstRules.isEmpty()) {
            return secondRules;
        }else if (secondRules==null || secondRules.isEmpty()) {
            return secondRules;
        }else{
            List<CachingRuleDescription> retval = new ArrayList<CachingRuleDescription>();
            retval.addAll(firstRules);
            retval.addAll(secondRules);
            return retval;
        }
    }



    private ClassCachingPolicyDescription first_;
    private ClassCachingPolicyDescription second_;

}
