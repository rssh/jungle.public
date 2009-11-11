package ua.gradsoft.caching;

import ua.gradsoft.caching.cfg.CachingConfiguration;
import java.io.Serializable;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;
import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Element;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class CacheInvocationHandler implements InvocationHandler
{
 
  public CacheInvocationHandler(ClassCachingPolicyDescription policy,
                                Object delegatedObject,
                                CachingWrapper wrapper
                               )  
  {
   cachingPolicy_=new ClassCachingPolicy(policy);
   delegatedObject_=delegatedObject;
   wrapper_ = wrapper;
  }

  public Object invoke(Object proxy, Method method, Object[] args) throws Throwable
  {
    if (LOG.isTraceEnabled()) {
        LOG.trace("invoking "+method.getName());
    }
    Object retval;
    MethodCachingPolicy mi = cachingPolicy_.getMethodCachingInstance(method);
    if (mi!=null) {
        mi = mi.getPolicyInterceptor().intercept(mi, proxy, method, args);
        String cacheName = mi.getCacheName();
        Cache cache = wrapper_.getCacheManager().getCache(cacheName);
        if (cache==null) {
            cache = lazyInitCache(cacheName);
            if (cache==null) {
                throw new CachingSystemException("Can't create cache "+cacheName);
            }
        }
        Serializable key=null;
        Serializable value=null;
        switch(mi.getCacheAction()) {
            case CACHE:
            {
              ValueBuilder keyBuilder = mi.getKeyBuilder();
              key = keyBuilder.build(delegatedObject_, method.getName(), args);
              Element e = cache.get(key);
              if (e!=null) {
                  if (LOG.isTraceEnabled()) {
                    LOG.trace("found value in cache");
                  }
                  retval = e.getValue();
              }else{
                  if (LOG.isTraceEnabled()) {
                    LOG.trace("value is not in cache");
                  }
                  try {
                    Object o = method.invoke(delegatedObject_, args);
                    if (o instanceof Serializable) {
                      retval = o;
                      value = (Serializable)o;
                      e = new Element(key,value);
                      cache.put(e);
                    }else{
                      throw new CachingSystemException("result of "+method.getName()+" method is not Serializable");  
                    }
                  }catch(IllegalAccessException ex){
                     throw new CachingSystemException("Exception during invocation of "+method.getName(),ex);
                  }catch(InvocationTargetException ex){
                     retval = null;
                     this.handleInvocationTargetException(method, ex);
                  }
              }
            }
            break;
            case CLEAR:
            {
                try {
                   retval = method.invoke(delegatedObject_, args);
                }catch(IllegalAccessException ex){
                     throw new CachingSystemException("Exception during invocation of "+method.getName(),ex);
                }catch(InvocationTargetException ex){
                     retval = null;
                     handleInvocationTargetException(method, ex);
                }finally{
                     if (LOG.isTraceEnabled()) {
                         LOG.trace("clearing cache");
                     }
                     cache.removeAll();
                }                
            }
            break;
            case UPDATE:
            {
              ValueBuilder keyBuilder = mi.getKeyBuilder();
              key = keyBuilder.build(delegatedObject_, method.getName(), args);
              ValueBuilder valueBuilder = mi.getValueBuilder();
              value = valueBuilder.build(delegatedObject_, method.getName(), args);
              try {
                   retval = method.invoke(delegatedObject_, args);
              }catch(IllegalAccessException ex){
                   throw new CachingSystemException("Exception during invocation of "+method.getName(),ex);
              }catch(InvocationTargetException ex){
                   retval = null;
                   handleInvocationTargetException(method, ex);
              }
              if (LOG.isTraceEnabled()) {
                  LOG.trace("putting new element in cache");
              }
              cache.put(new Element(key,value));
            }
            break;
            case REMOVE:
            {
              ValueBuilder keyBuilder = mi.getKeyBuilder();
              key = keyBuilder.build(delegatedObject_, method.getName(), args);
              try {
                   retval = method.invoke(delegatedObject_, args);
              }catch(IllegalAccessException ex){
                   throw new CachingSystemException("Exception during invocation of "+method.getName(),ex);
              }catch(InvocationTargetException ex){
                   retval = null;
                   handleInvocationTargetException(method, ex);
              }finally{
                  if (LOG.isTraceEnabled()) {
                      LOG.trace("removing key from cache");
                  }
                  cache.remove(key);
              }
            }
            case WITHOUT:
              try {
                   retval = method.invoke(delegatedObject_, args);
              }catch(IllegalAccessException ex){
                   throw new CachingSystemException("Exception during invocation of "+method.getName(),ex);
              }catch(InvocationTargetException ex){
                  retval=null; // make DFA analyzer happy
                  handleInvocationTargetException(method, ex);
              }
            break;
            default:
              throw new CachingSystemException("Unknown cache action:"+mi.getCacheAction().toString()); 

        }
        evalRules(mi,key,value);
    } else {
       try {
         retval = method.invoke(delegatedObject_, args);
       }catch(IllegalAccessException ex){
         throw new CachingSystemException("Exception during invocation of "+method.getName(),ex);
       }catch(InvocationTargetException ex){
         retval=null; // make DFA analyzer happy.
         handleInvocationTargetException(method, ex);
       }
    }
    return retval;
  }

  private void handleInvocationTargetException(Method m, InvocationTargetException ex) throws Throwable
  {
    Class<?>[] exTypes = m.getExceptionTypes();
    Throwable exCause = ex.getCause();
    if (LOG.isDebugEnabled()) {
        LOG.debug("excdption during invoking cache method",exCause);
    }
    Class<?> exCauseClass = exCause.getClass();
    for(int i=0; i<exTypes.length; ++i) {
        if (exTypes[i].isAssignableFrom(exCauseClass)) {            
            throw exCause;
        }
    }
    throw new CachingSystemException("Exception during invocation of "+m.getName(),ex);    
  }

    private void evalRules(MethodCachingPolicy mi, Serializable key, Serializable value)
    {
      List<CachingRuleDescription> rules = cachingPolicy_.getRules();
      if (rules==null) {
          return;
      }
      for(CachingRuleDescription rule:rules) {
         CachingRuleDescription.Part leftPart = rule.getLeft();
         if (leftPart.getCacheName().equals(mi.getCacheName()) && leftPart.getCacheAction().equals(mi.getCacheAction())) {
             List<CachingRuleDescription.Part> rights = rule.getRight();
             for(CachingRuleDescription.Part rigth:rights) {
                Cache cache = CacheManager.getInstance().getCache(rigth.getCacheName());
                switch(rigth.getCacheAction()) {
                    case CACHE:
                    {
                      if (key!=null && value!=null) {
                        cache.put(new Element(key,value));
                      } else {
                        throw new CachingSystemException("Can't eval cache rule: empty key or value");
                      }
                    }
                    break;
                    case CLEAR:
                    {
                      cache.removeAll();
                    }
                    break;
                    case REMOVE:
                    {
                      if (key!=null) {
                          cache.remove(key);
                      } else {
                          throw new CachingSystemException("Can't eval 'remove' cache rule: empty key");
                      }
                    }
                    break;
                    case UPDATE:
                    {
                      if (key!=null && value!=null) {
                        cache.put(new Element(key,value));
                      } else {
                        throw new CachingSystemException("Can't eval cache rule: empty key or value");
                      }
                    }
                    break;
                    default:
                        break;
                }
             }
         }
      }
    }

    private Cache lazyInitCache(String cacheName)
    {
      if (LOG.isInfoEnabled()) {
          LOG.info("creating cache "+cacheName);
      }
      CachingConfiguration configuration = wrapper_.getConfiguration();
      CachingConfiguration.CacheConfig cacheConfig = configuration.getCacheConfig(cacheName);
      Cache newCache = new Cache(cacheName,
                                 cacheConfig.getMaxElementsInMemoryForCache(),
                                 cacheConfig.getMemoryStoreEvictionPolicy(),
                                 cacheConfig.getOverflowToDisk(),
                                 null,
                                 cacheConfig.getEteral(),
                                 cacheConfig.getTimeToLive(),
                                 cacheConfig.getTimeToIdle(),
                                 cacheConfig.getDiskPersistent(),
                                 cacheConfig.getDiskExpireThreadInterval(),
                                 null
                                 );
      wrapper_.getCacheManager().addCache(newCache);
      return newCache;
    }



  private Log LOG = LogFactory.getLog(CacheInvocationHandler.class);
  private ClassCachingPolicy cachingPolicy_;
  private Object delegatedObject_;
  private CachingWrapper wrapper_;


}
