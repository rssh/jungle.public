package ua.gradsoft.caching.cfg;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import javax.xml.bind.annotation.adapters.XmlAdapter;
import ua.gradsoft.caching.cfg.CachingConfiguration.CacheConfig;
import ua.gradsoft.caching.cfg.XmlCacheConfigEntries.XmlCacheConfigEntry;

/**
 *Map adapter to to config entries.
 * @author rssh
 */
public class XmlCacheConfigEntriesMapAdapter extends XmlAdapter<XmlCacheConfigEntries,Map<String,CachingConfiguration.CacheConfig>>
{

    @Override
    public XmlCacheConfigEntries marshal(Map<String, CacheConfig> v) throws Exception {
        XmlCacheConfigEntries retval = new XmlCacheConfigEntries();
        List<XmlCacheConfigEntry> entries = new ArrayList<XmlCacheConfigEntry>();
        for(Map.Entry<String,CacheConfig> e: v.entrySet()) {
           entries.add(new XmlCacheConfigEntry(e.getKey(),e.getValue()));
        }
        retval.setEntries(entries);
        return retval;
    }

    @Override
    public Map<String, CacheConfig> unmarshal(XmlCacheConfigEntries v) throws Exception {
        Map<String,CacheConfig> retval = new TreeMap<String,CacheConfig>();
        for(XmlCacheConfigEntry e: v.getEntries()) {
            retval.put(e.getCacheName(), e.getCacheConfig());
        }
        return retval;
    }

}
