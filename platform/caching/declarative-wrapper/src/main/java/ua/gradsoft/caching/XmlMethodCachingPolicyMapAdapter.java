/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ua.gradsoft.caching;

import ua.gradsoft.caching.XmlMethodCachingPolicyEntry;
import ua.gradsoft.caching.XmlMethodCachingPolicies;
import java.util.ArrayList;
import java.util.Map;
import java.util.TreeMap;
import javax.xml.bind.annotation.adapters.XmlAdapter;
import ua.gradsoft.caching.MethodCachingPolicyDescription;

/**
 *
 * @author rssh
 */
public class XmlMethodCachingPolicyMapAdapter extends XmlAdapter<XmlMethodCachingPolicies,Map<String, MethodCachingPolicyDescription>>
{

    public Map<String,MethodCachingPolicyDescription> unmarshal(XmlMethodCachingPolicies policies)
    {
       Map<String,MethodCachingPolicyDescription> retval = new TreeMap<String,MethodCachingPolicyDescription>();
       for(XmlMethodCachingPolicyEntry e: policies.entries) {
           retval.put(e.key, e.methodCachingPolicy);
       }
       return retval;
    }

    public XmlMethodCachingPolicies marshal(Map<String,MethodCachingPolicyDescription> methodsMap)
    {
       XmlMethodCachingPolicies retval = new XmlMethodCachingPolicies();
       retval.entries = new ArrayList<XmlMethodCachingPolicyEntry>();
       for(Map.Entry<String,MethodCachingPolicyDescription> e:methodsMap.entrySet()){       
           XmlMethodCachingPolicyEntry re = new XmlMethodCachingPolicyEntry();
           re.key=e.getKey();
           re.methodCachingPolicy=e.getValue();
           retval.entries.add(re);
       }
       return retval;
    }

}
