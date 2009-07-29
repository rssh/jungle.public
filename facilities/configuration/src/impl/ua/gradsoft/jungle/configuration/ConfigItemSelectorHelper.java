package ua.gradsoft.jungle.configuration;

import java.util.Map;
import java.util.TreeMap;

/**
 *static class for common operations on all selectors
 */
public class ConfigItemSelectorHelper {

    public static Map<String,Object>  createOptions(ConfigItemSelector selector)
    {
       Map<String,Object> retval = new TreeMap<String,Object>(); 
       if (selector.getFirstResult()>0) {
           retval.put("firstResult", selector.getFirstResult());
       } 
       if (selector.getMaxResults()>0) {
           retval.put("maxResults", selector.getMaxResults());
       }
       return retval;
    }

}
