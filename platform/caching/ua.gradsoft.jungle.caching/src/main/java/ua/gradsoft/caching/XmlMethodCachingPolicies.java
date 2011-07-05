package ua.gradsoft.caching;

import java.util.List;
import javax.xml.bind.annotation.XmlElement;

/**
 *
 * @author rssh
 */
public class XmlMethodCachingPolicies {


    @XmlElement(name="entry")
    public List<XmlMethodCachingPolicyEntry> entries;

}
