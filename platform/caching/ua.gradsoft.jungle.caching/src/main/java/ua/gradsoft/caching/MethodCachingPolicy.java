package ua.gradsoft.caching;

import java.io.Serializable;

/**
 *Instance for method caching
 * @author rssh
 */
public class MethodCachingPolicy implements Cloneable
{


    public String getCacheName()
    { return cacheName_; }

    public void setCacheName(String cacheName)
    { cacheName_=cacheName; }

    public CacheAction getCacheAction()
    { return cacheAction_; }

    public void setCacheAction(CacheAction cacheAction)
    { cacheAction_=cacheAction; }

    public ValueBuilder getKeyBuilder()
    {
      return keyBuilder_;  
    }
    
    public void  setKeyBuilder(ValueBuilder keyBuilder)
    {
      keyBuilder_ = keyBuilder;  
    }

    public ValueBuilder getValueBuilder()
    {
      return valueBuilder_;  
    }

    public void  setValueBuilder(ValueBuilder valueBuilder)
    {
      valueBuilder_ = valueBuilder;
    }


    public CachingPolicyInterceptor getPolicyInterceptor()
    {
      return policyInterceptor_;
    }

    public <OT,CT extends Serializable> ValueTransformer<OT,CT> getResultTransformer()
    {
      if (resultTransformer_==null) {
          return null;
      } else {
          return (ValueTransformer<OT,CT>)resultTransformer_;
      }
    }

    public <OT,CT extends Serializable> void setResultTransformer(ValueTransformer<OT,CT> resultTransformer)
    {
      resultTransformer_=resultTransformer;
    }


    public MethodCachingPolicy(MethodCachingPolicyDescription description)
    {
      cacheName_=description.getCacheName();
      cacheAction_=description.getCacheAction();
      if (description.getKeyBuilderClass()!=null) {
        try {
          keyBuilder_ =  description.getKeyBuilderClass().newInstance();
        }catch(IllegalAccessException ex){
            throw new CachingSystemException("Can't create key builder "+description.getKeyBuilderClass().getName(),ex);
        }catch(InstantiationException ex){
            throw new CachingSystemException("Can't create key builder "+description.getKeyBuilderClass().getName(),ex);
        }
      }else{
          keyBuilder_=null;
      }
      if (description.getValueBuilderClass()!=null) {
        try {
          valueBuilder_ =  description.getValueBuilderClass().newInstance();
        }catch(IllegalAccessException ex){
            throw new CachingSystemException("Can't create value builder "+description.getValueBuilderClass().getName(),ex);
        }catch(InstantiationException ex){
            throw new CachingSystemException("Can't create value builder "+description.getValueBuilderClass().getName(),ex);
        }
      }
      if (description.getResultTransformerClass()!=null) {
        try {
             resultTransformer_= description.getResultTransformerClass().newInstance();
        }catch(IllegalAccessException ex){
            throw new CachingSystemException("Can't create result transformer "+description.getResultTransformerClass().getName(),ex);
        }catch(InstantiationException ex){
            throw new CachingSystemException("Can't create result transformer "+description.getResultTransformerClass().getName(),ex);
        }
      }
      if (description.getPolicyInterceptor()!=null) {
        try {
             policyInterceptor_= description.getPolicyInterceptor().newInstance();
        }catch(IllegalAccessException ex){
            throw new CachingSystemException("Can't create policy interceptor "+description.getPolicyInterceptor().getName(),ex);
        }catch(InstantiationException ex){
            throw new CachingSystemException("Can't create policy interceptor "+description.getPolicyInterceptor().getName(),ex);
        }
      }
    }

    public MethodCachingPolicy(MethodCachingPolicy x)
    {
       cacheAction_=x.cacheAction_;
       cacheName_=x.cacheName_;
       keyBuilder_=x.keyBuilder_;
       valueBuilder_=x.valueBuilder_;
       resultTransformer_=x.resultTransformer_;
       policyInterceptor_=x.policyInterceptor_;
    }


    @Override
    public Object clone()
    {
       return new MethodCachingPolicy(this);
    }

    
    private String cacheName_;
    private CacheAction cacheAction_;
    private ValueBuilder keyBuilder_;
    private ValueBuilder valueBuilder_;
    private ValueTransformer<?,? extends Serializable> resultTransformer_;
    private CachingPolicyInterceptor policyInterceptor_;

}
