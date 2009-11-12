package ua.gradsoft.caching;

import java.lang.reflect.Method;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

/**
 *
 * @author rssh
 */
@XmlType(name="CachingPolicy")
public class XmlClassCachingPolicyDescription implements ClassCachingPolicyDescription
{

    @XmlElement(name="Rules")
    public List<CachingRuleDescription> getRules()
    { return cachingRules_; }

    public void setRules(List<CachingRuleDescription> cachingRules)
    { cachingRules_=cachingRules; }

    @XmlElement(name="MethodsCachingPolicy")
    @XmlJavaTypeAdapter(XmlMethodCachingPolicyMapAdapter.class)
    public Map<String,MethodCachingPolicyDescription> getMethodsCachingPolicy()
    { return methodsCachingPolicy_; }

    public void setMethodsCachingPolicy(Map<String,MethodCachingPolicyDescription> methodsCachingPolicy)
    { methodsCachingPolicy_=methodsCachingPolicy; }


    public MethodCachingPolicyDescription getMethodCachingPolicyDescription(Method m)
    {
      return methodsCachingPolicy_.get(m.getName());
    }

    private List<CachingRuleDescription> cachingRules_ = new LinkedList<CachingRuleDescription>();
    
    private Map<String,MethodCachingPolicyDescription> methodsCachingPolicy_;

}
