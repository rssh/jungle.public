package ua.gradsoft.caching.annotations;

import ua.gradsoft.caching.CacheAction;

/**
 * Action in right part of cache rule.
 */
public @interface CachingRuleAction {

   String cacheName();

   CacheAction action();

}
