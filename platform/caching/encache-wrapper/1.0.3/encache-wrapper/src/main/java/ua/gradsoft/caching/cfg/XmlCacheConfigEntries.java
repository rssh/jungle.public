package ua.gradsoft.caching.cfg;

import java.io.Serializable;
import java.util.List;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;


public class XmlCacheConfigEntries implements Serializable
{

    public static class XmlCacheConfigEntry implements Serializable
    {

       public XmlCacheConfigEntry(String name, CachingConfiguration.CacheConfig value)
       { cacheName_=name;
         cacheConfig_=value;
       }

       @XmlAttribute(name="cacheName")
       public String getCacheName()
       { return cacheName_; }

       public void setCacheName(String cacheName)
       { cacheName_=cacheName; }

       @XmlElement(name="CacheConfig")
       public CachingConfiguration.CacheConfig getCacheConfig()
       { return cacheConfig_; }

       public void setCacheConfig(CachingConfiguration.CacheConfig  cacheConfig)
       { cacheConfig_ = cacheConfig; }


       private String cacheName_;
       private CachingConfiguration.CacheConfig cacheConfig_;
    }

    @XmlElement(name="entry")
    public List<XmlCacheConfigEntry> getEntries()
    { return entries_; }
    
    void setEntries(List<XmlCacheConfigEntry> entries)
    { entries_=entries; }

    private List<XmlCacheConfigEntry> entries_;

}
