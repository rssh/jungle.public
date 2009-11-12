package ua.gradsoft.caching;

import java.io.Serializable;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;

@XmlType(name="MethodCachingPolicy")
@XmlAccessorType(XmlAccessType.PROPERTY)
public class MethodCachingPolicyDescription implements Serializable
{

  @XmlAttribute(name="action", required=true)
  public CacheAction getCacheAction()
   { return cacheAction_; }

  public void setCacheAction(CacheAction cacheAction)
  { cacheAction_= cacheAction; }

  @XmlAttribute(name="cacheName", required=true)
  public String getCacheName()
   { return cacheName_; }

  public void setCacheName(String cacheName)
  { cacheName_= cacheName; }

  @XmlAttribute(name="keyBuilder")
  public Class<? extends ValueBuilder>  getKeyBuilderClass()
   { return keyBuilderClass_; }

  public void setKeyBuilderClass(Class<? extends ValueBuilder> keyBuilderClass)
  { keyBuilderClass_ = keyBuilderClass; }

  @XmlAttribute(name="valueBuilder")
  public Class<? extends ValueBuilder>  getValueBuilderClass()
  { return valueBuilderClass_; }

  public void setValueBuilderClass(Class<? extends ValueBuilder> valueBuilderClass)
  { valueBuilderClass_ = valueBuilderClass; }

  @XmlAttribute(name="resultTransformer")
  public Class<? extends ValueTransformer>  getResultTransformerClass()
   { return resultTransformerClass_; }

  public void setResultTransformerClass(Class<? extends ValueTransformer> resultTransformerClass)
  {
     resultTransformerClass_=resultTransformerClass;
  }

  @XmlAttribute(name="policyInterceptor")
  public Class<? extends CachingPolicyInterceptor>  getPolicyInterceptor()
   { return policyInterceptor_; }

  public void setCachePolicyInterceptor(Class<? extends CachingPolicyInterceptor> policyInterceptor)
  {
     policyInterceptor_=policyInterceptor; 
  }

  private CacheAction cacheAction_;
  private String cacheName_; 
  private Class<? extends ValueBuilder>  keyBuilderClass_;
  private Class<? extends ValueBuilder>  valueBuilderClass_;
  private Class<? extends ValueTransformer>  resultTransformerClass_;
  private Class<? extends CachingPolicyInterceptor>  policyInterceptor_;

}
