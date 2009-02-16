package ua.gradsoft.caching.annotations;

import ua.gradsoft.caching.CacheAction;

/**
 * Rule
 */
public @interface CachingRule {

    public String triggerCache();
    
    public CacheAction triggerAction();
    
    public CachingRuleAction[]  actions();

}
