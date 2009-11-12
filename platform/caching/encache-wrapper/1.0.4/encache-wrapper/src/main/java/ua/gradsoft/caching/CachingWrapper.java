
package ua.gradsoft.caching;

import ua.gradsoft.caching.cfg.CachingConfiguration;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.management.ManagementFactory;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;
import javax.management.MBeanServer;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import net.sf.ehcache.CacheManager;
import net.sf.ehcache.management.ManagementService;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 *Facade for creation of caching wrappers arround java objects,
 * where some methods are cached.
 * @author rssh
 */
public class CachingWrapper {


    public 
       <InterfaceType,ObjType extends InterfaceType>
                        InterfaceType  createCached(
                                        Class<InterfaceType> interfaceType,
                                        ObjType              obj
                                        )
    {
      return createCached(interfaceType,obj,(ClassCachingPolicyDescription)null);
    }



    public
       <InterfaceType,ObjType extends InterfaceType> 
                        InterfaceType  createCached(
                                        Class<InterfaceType> interfaceType,
                                        ObjType              obj,
                                        String               xmlConfiguration
                                        )
    {
       ClassCachingPolicyDescription clpd = null;
       if (xmlConfiguration!=null) {
           JAXBContext jc = null;
           Unmarshaller unmarshaller = null;
           try {
             jc = JAXBContext.newInstance("ua.gradsoft.caching");
             unmarshaller = jc.createUnmarshaller();
           }catch(JAXBException ex){
               throw new CachingSystemException("Can't initialize jaxb",ex);
           }
           Object o = null;
           XmlClassCachingPolicies policies = null;
           if (xmlConfiguration.startsWith("classpath:")) {
               String nameInClasspath = xmlConfiguration.substring(10);
               InputStream xml = Thread.currentThread().getContextClassLoader().getResourceAsStream(nameInClasspath);
               try {
                 o = unmarshaller.unmarshal(xml);
               } catch(JAXBException ex){
                   throw new CachingSystemException("Exception during unmarshall of "+xmlConfiguration,ex);
               } finally {
                 try {
                   xml.close();
                 }catch(IOException ex){
                     throw new CachingSystemException("Can't close xml stream from "+xmlConfiguration);
                 }
               }
           }else{
               File xml = new File(xmlConfiguration);
               try {
                 o = unmarshaller.unmarshal(xml);
               } catch(JAXBException ex){
                 throw new CachingSystemException("Exception during unmarshall of "+xmlConfiguration,ex);
               }
           }
           if (o instanceof XmlClassCachingPolicies) {
               policies = (XmlClassCachingPolicies)o;
           }else{
               throw new CachingSystemException("bad content of "+xmlConfiguration+", must be CachingPolicies");
           }
           if (policies.getEntries().isEmpty()) {
               throw new CachingSystemException("bad content of "+xmlConfiguration+", policies must not be empty");
           }
           for(XmlClassCachingPolicies.Entry e: policies.getEntries()) {
              Class cachedClass = e.getCachedClass();
              ClassCachingPolicyDescription clpdCurrent = e.getClassCachingPolicy();
              if (cachedClass.isAssignableFrom(obj.getClass())) {
                  clpd = clpdCurrent;
                  break;
              }
           }
           if (clpd==null) {
               // not found
               throw new CachingSystemException("Can't find xml description of caching policy for class "+obj.getClass());
           }
           return createCached(interfaceType,obj,clpd);
       } else {
           return createCached(interfaceType,obj,(ClassCachingPolicyDescription)null);
       }
    }

    public 
       <InterfaceType,ObjType extends InterfaceType>
                               InterfaceType createCached(
                                        Class<InterfaceType> interfaceType,
                                        ObjType              obj,
                                        ClassCachingPolicyDescription policyDescription
                              )
    {
        if (LOG.isTraceEnabled()) {
            LOG.trace("creation of cached object.");
        }
        InvocationHandler h = new CacheInvocationHandler(
                new FederatedClassCachingPolicyDescription(
                        new AnnotationBasedClassCachingPolicyDescription(obj.getClass()),
                        policyDescription
                    ), obj, this
                              );
        Class[] interfaces = new Class[1];
        interfaces[0]=interfaceType;
        Object retval = Proxy.newProxyInstance(Thread.currentThread().getContextClassLoader(),
                                          interfaces, h);
        return (InterfaceType)retval;
    }

    public CachingConfiguration getConfiguration()
    {
      if (configuration_!=null) {
          return configuration_;
      }  
      synchronized(this) {
          if (configuration_==null) {
              configuration_ = new CachingConfiguration();
          }
      }
      return configuration_;
    }

    public void setConfiguration(CachingConfiguration configuration)
    {
        configuration_=configuration;
    }

    public CacheManager getCacheManager()
    {
      if (cacheManager_==null) {
          synchronized(this) {
              lazyInitCacheManager();
          }
      }
      return cacheManager_;
    }

    public void shutdown()
    {
      if (cacheManager_!=null) {
          cacheManager_.shutdown();
      }  
    }

    public void lazyInitCacheManager()
    {
      if (LOG.isDebugEnabled())  {
          LOG.debug("init cache manager start.");
      }

      if (cacheManager_==null) {
        CachingConfiguration cfg = getInstance().getConfiguration();
        if (cfg.getEhcacheConfigName()==null) {
            cacheManager_ = CacheManager.getInstance();
        }else{
            String configName = cfg.getEhcacheConfigName();
            if (configName.startsWith("classpath:")) {
                String nameInClasspath = configName.substring(10);
                InputStream inputStream = Thread.currentThread().getContextClassLoader().getResourceAsStream(nameInClasspath);
                cacheManager_ = CacheManager.create(inputStream);
            }else{
                cacheManager_ = CacheManager.create(configName);
            }
        }
        MBeanServer mBeanServer = ManagementFactory.getPlatformMBeanServer();
        if (mBeanServer!=null) {
            ManagementService.registerMBeans(cacheManager_, mBeanServer,
                                               true  , true , true, true);
        } else {
            if (LOG.isWarnEnabled()) {
                LOG.warn("Can't determinate platform mbean server");
            }
        }
      }

      if (LOG.isDebugEnabled())  {
          LOG.debug("init cache manager stop.");
      }


    }

    public static CachingWrapper getInstance()
    {     
      if (singleton_!=null) {
          return singleton_;
      }  
      synchronized(CachingWrapper.class){
          if (singleton_==null) {        
              singleton_ = new CachingWrapper();
          }
      }      
      return singleton_;
    }
    
    private static CachingWrapper singleton_ = null;

    private Log LOG = LogFactory.getLog(CachingWrapper.class);
    private CachingConfiguration configuration_ = null;
    private CacheManager         cacheManager_ = null;

}
