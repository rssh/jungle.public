package ua.gradsoft.caching;

import java.util.List;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

/**
 *Caching policies for classes
 * @author rssh
 */
@XmlRootElement(name="CachingPolicies")
@XmlType(name="CachingPolicies")
public class XmlClassCachingPolicies {


    public static class Entry {

      @XmlJavaTypeAdapter(XmlClassAdapter.class)
      @XmlAttribute(name="class")
      public Class getCachedClass()
      { return clazz_; }
      
      public void setCachedClass(Class clazz)
      { clazz_=clazz; }
    
      @XmlElement(name="ClassCachingPolicy")
      public  XmlClassCachingPolicyDescription getClassCachingPolicy()
      { return cachingPolicyDescription_; }
      
      public void setClassCachingPolicy(XmlClassCachingPolicyDescription cachingPolicyDescription)
      { cachingPolicyDescription_=cachingPolicyDescription; }
    
      private Class clazz_;
      private XmlClassCachingPolicyDescription cachingPolicyDescription_;
    }

    @XmlElement
    public List<Entry> getEntries()
    { return entries_; }

    public void setEntries(List<Entry> entries)
    { entries_=entries; }

    private List<Entry> entries_;

}
