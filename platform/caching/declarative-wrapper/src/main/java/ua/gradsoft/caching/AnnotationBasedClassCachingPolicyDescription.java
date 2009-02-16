package ua.gradsoft.caching;

import java.lang.reflect.Method;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import ua.gradsoft.caching.annotations.Caching;
import ua.gradsoft.caching.annotations.CachingRule;
import ua.gradsoft.caching.annotations.CachingRuleAction;
import ua.gradsoft.caching.annotations.CachingRules;

/**
 *Policy description, based on annotations
 * @author rssh
 */
public class AnnotationBasedClassCachingPolicyDescription implements ClassCachingPolicyDescription
{

    public AnnotationBasedClassCachingPolicyDescription(Class clazz)
    {
      class_=clazz;
    }

    public MethodCachingPolicyDescription getMethodCachingPolicyDescription(Method m) {
         if (LOG.isTraceEnabled()) {
             LOG.trace("getMethodCachingPolicyDescription for "+m.getName());
         }
         // find same metod in our class.
         Caching caching = m.getAnnotation(Caching.class);
         if (caching==null) {
            if (LOG.isTraceEnabled()) {
                LOG.trace("Caching annotation not found in "+m.getDeclaringClass()+" search in "+class_.getName());
            }
            Method ourClassMethod = null;
            try {
              ourClassMethod = class_.getMethod(m.getName(), m.getParameterTypes());
            } catch (NoSuchMethodException ex){
              // impossible: class must implements interface
              throw new CachingSystemException("can;t find method "+m.getName()+" in class "+class_.getName());
            }
            caching = ourClassMethod.getAnnotation(Caching.class);
         }


         if (caching==null) {
             if (LOG.isTraceEnabled()) {
                 LOG.trace("Caching annotations not found, return 0");
             }
             return null;
         }else{
             MethodCachingPolicyDescription retval = new MethodCachingPolicyDescription();
             retval.setCacheAction(caching.action());
             retval.setCacheName(caching.cacheName());
             retval.setKeyBuilderClass(caching.keyBuilder());
             retval.setValueBuilderClass(caching.valueBuilder());
             retval.setResultTransformerClass(caching.objectTransformer());
             retval.setCachePolicyInterceptor(caching.policyInterceptor());
             if (retval.getCacheName().length()==0) {
                 Caching classCaching = (Caching)class_.getAnnotation(Caching.class);
                 if (classCaching!=null) {
                     retval.setCacheName(classCaching.cacheName());
                 }
             }
             if (LOG.isTraceEnabled()) {
                 LOG.trace("annotations found, action is "+caching.action());
             }
             return retval;
         }
    }

    public List<CachingRuleDescription> getRules() {
        CachingRules rules = (CachingRules)class_.getAnnotation(CachingRules.class);
        List<CachingRuleDescription> retval = null;
        if (rules!=null) {
            retval = new LinkedList<CachingRuleDescription>();
            for(CachingRule rule: rules.value()) {
               CachingRuleDescription newRuleDescription = buildCachingRuleDescription(rule);
               retval.add(newRuleDescription);
            }
        }else{
            CachingRule rule = (CachingRule)class_.getAnnotation(CachingRule.class);
            if (rule!=null) {
                retval=Collections.singletonList(buildCachingRuleDescription(rule));
            }
        }
        return retval;
    }

    private CachingRuleDescription buildCachingRuleDescription(CachingRule rule)
    {
      CachingRuleDescription retval = new CachingRuleDescription();
      CachingRuleDescription.Part left = new CachingRuleDescription.Part();
      left.setCacheAction(rule.triggerAction());
      left.setCacheName(rule.triggerCache());
      retval.setLeft(left);
      List<CachingRuleDescription.Part> rights = new LinkedList<CachingRuleDescription.Part>();
      for(CachingRuleAction a: rule.actions()) {
          CachingRuleDescription.Part x = new CachingRuleDescription.Part();
          x.setCacheAction(a.action());
          x.setCacheName(a.cacheName());
          rights.add(x);
      }
      retval.setRight(rights);
      return retval;
    }

    private Class class_;

    private Log LOG = LogFactory.getLog(AnnotationBasedClassCachingPolicyDescription.class);

}
