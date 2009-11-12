package ua.gradsoft.caching.annotations;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import ua.gradsoft.caching.CacheAction;
import ua.gradsoft.caching.ValueBuilder;
import ua.gradsoft.caching.ValueTransformer;
import ua.gradsoft.caching.CachingPolicyInterceptor;
import ua.gradsoft.caching.util.EmptyValueBuilder;
import ua.gradsoft.caching.util.AllArguments;
import ua.gradsoft.caching.util.IdValueTransformer;
import ua.gradsoft.caching.util.IdCachingPolicyInterceptor;

/**
 *Method annotation for declarative cache.
 *
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface Caching
{

    /**
     * name of cache.
     */
    String cacheName() default "";
    
    /**
     * action for caching.  
     */
    CacheAction  action() default CacheAction.WITHOUT;
    
    /**
     * Class for building of key from method argument
     */
    Class<? extends ValueBuilder> keyBuilder() default EmptyValueBuilder.class;
    
    /**
     * Class for building of value from method argument.
     * (used only if action==UPDATE)  
     */
    Class<? extends ValueBuilder> valueBuilder() default EmptyValueBuilder.class;
    
    /**
     * Class for transforming result of funxction into form, suitable for hashing.    
     */
    Class<? extends ValueTransformer>  objectTransformer() default IdValueTransformer.class;

    /**
     * Class for intercepting caching policy before handling.
     */
    Class<? extends CachingPolicyInterceptor>  policyInterceptor() default IdCachingPolicyInterceptor.class;



}
