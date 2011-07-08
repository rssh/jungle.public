package ua.gradsoft.caching;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

/**
 *Entry for XML serialization of methodCachingPolicy
 * @author rssh
 */
@XmlType(name="MethodCachingPolicyEntry")
public class XmlMethodCachingPolicyEntry {

       @XmlAttribute(name="methodName")
       public String key;

       @XmlElement
       public MethodCachingPolicyDescription methodCachingPolicy;



}
