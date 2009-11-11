package ua.gradsoft.caching.cfg;

import java.io.Serializable;
import java.util.Map;
import java.util.TreeMap;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import net.sf.ehcache.store.MemoryStoreEvictionPolicy;

/**
 *Configuration for caching subsystem.
 * @author rssh
 */
@XmlRootElement
@XmlAccessorType(XmlAccessType.NONE)
public class CachingConfiguration implements Serializable {

    @XmlType(name="CacheConfig")
    public static class CacheConfig implements Serializable
    {
      @XmlAttribute
      public int getMaxElementsInMemoryForCache()
      { return maxElementsInMemoryForCache_; }

      public void setMaxElementsInMemoryForCache(int maxElementsInMemoryForCache)
      {
       maxElementsInMemoryForCache_=maxElementsInMemoryForCache;
      }

      @XmlAttribute
      public MemoryStoreEvictionPolicy getMemoryStoreEvictionPolicy()
      {
        return memoryStoreEvictionPolicy_;
      }

      public void setMemoryStoreEvictionPolicy(MemoryStoreEvictionPolicy memoryStoreEvictionPolicy)
      {
        memoryStoreEvictionPolicy_ = memoryStoreEvictionPolicy;
      }

      @XmlAttribute
      public boolean getOverflowToDisk()
      { return overflowToDisk_; }
      
      public void setOverflowToDisk(boolean overflowToDisk)
      { overflowToDisk_ = overflowToDisk; }

      @XmlAttribute
      public boolean getEteral()
      { return eteral_; }

      public void setEteral(boolean eteral)
      { eteral_=eteral; }

      @XmlAttribute
      public long getTimeToLive()
      { return timeToLive_; }

      public void setTimeToLive(long timeToLive)
      { timeToLive_=timeToLive; }

      @XmlAttribute
      public long getTimeToIdle()
      { return timeToIdle_; }

      public void setTimeToIdle(long timeToIdle)
      { timeToIdle_=timeToIdle; }

      @XmlAttribute
      public boolean getDiskPersistent()
      { return diskPersistent_; }

      public void  setDiskPersistent(boolean diskPersistent)
      { diskPersistent_ = diskPersistent; }

      @XmlAttribute
      public long getDiskExpireThreadInterval()
      { return diskExpireThreadInterval_; }

      public void setDiskExpireThreadInterval(long diskExpireThreadInterval)
      { diskExpireThreadInterval_=diskExpireThreadInterval; }

      private long    diskExpireThreadInterval_ = CachingConfiguration.DEFAULT_DISK_EXPIRE_THREAD_INTERVAL;
      private boolean diskPersistent_ = CachingConfiguration.DEFAULT_DISK_PERSISTENT;
      private int maxElementsInMemoryForCache_ = CachingConfiguration.DEFULT_MAX_ELEMENTS_IN_MEMORY_FOR_CACHE;
      private MemoryStoreEvictionPolicy memoryStoreEvictionPolicy_ = CachingConfiguration.DEFAULT_EVICTION_POLICY;
      private boolean overflowToDisk_ = CachingConfiguration.DEFAULT_OVERFLOW_TO_DISK;
      private boolean eteral_ = CachingConfiguration.DEFAULT_ETERAL;
      private long    timeToLive_ = CachingConfiguration.DEFAULT_TIME_TO_LIVE;
      private long    timeToIdle_ = CachingConfiguration.DEFAULT_TIME_TO_IDLE;

      private static final long serialVersionUID = 200901080005L;
    }

    @XmlElement
    public CacheConfig getDefaultCacheConfig()
    { return defaultCacheConfig_; }

    public void setDefaultCacheConfig(CacheConfig cacheConfig)
    { defaultCacheConfig_=cacheConfig; }

    @XmlElement
    @XmlJavaTypeAdapter(XmlCacheConfigEntriesMapAdapter.class)
    public Map<String,CacheConfig>  getPerCacheConfig()
    {
      return perCacheConfigs_;
    }

    public void   setPerCacheConfig(Map<String,CacheConfig> perCacheConfig)
    {
      perCacheConfigs_ = perCacheConfig;
    }


    public CacheConfig getCacheConfig(String cacheName)
    {
      CacheConfig retval = perCacheConfigs_.get(cacheName);
      if (retval!=null) {
          return retval;
      }else{
          return defaultCacheConfig_;
      }
    }

    public void setCacheConfig(String cacheName, CacheConfig cacheConfig)
    {
      perCacheConfigs_.put(cacheName, cacheConfig);
    }

    @XmlAttribute(name="ehcacheConfig")
    public String getEhcacheConfigName()
    { return ehcacheConfigName_; }

    public void setEhcacheConfig(String configName)
    { ehcacheConfigName_=configName; }



    private String      ehcacheConfigName_ = null;
    private CacheConfig defaultCacheConfig_ = new CacheConfig();
    private Map<String, CacheConfig> perCacheConfigs_ = new TreeMap<String,CacheConfig>();


    public static final long    DEFAULT_DISK_EXPIRE_THREAD_INTERVAL = 60*5;
    public static final boolean DEFAULT_DISK_PERSISTENT = false;
    public static final long    DEFAULT_TIME_TO_IDLE = (60*10)*1;
    public static final long    DEFAULT_TIME_TO_LIVE = (60*60)*3;
    public static final boolean DEFAULT_ETERAL = false;
    public static final boolean DEFAULT_OVERFLOW_TO_DISK = false;
    public static final MemoryStoreEvictionPolicy DEFAULT_EVICTION_POLICY = MemoryStoreEvictionPolicy.LRU;
    public static final int DEFULT_MAX_ELEMENTS_IN_MEMORY_FOR_CACHE = 1000;

    private static final long serialVersionUID = 200901080005L;
}
