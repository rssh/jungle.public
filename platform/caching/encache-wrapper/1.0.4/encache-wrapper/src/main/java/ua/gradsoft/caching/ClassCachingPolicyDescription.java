package ua.gradsoft.caching;

import java.lang.reflect.Method;
import java.util.List;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 */
public interface ClassCachingPolicyDescription {

    public List<CachingRuleDescription>  getRules();

    public MethodCachingPolicyDescription getMethodCachingPolicyDescription(Method m);
}
