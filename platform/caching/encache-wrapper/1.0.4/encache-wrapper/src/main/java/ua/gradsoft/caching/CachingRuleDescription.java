
package ua.gradsoft.caching;

import ua.gradsoft.caching.CacheAction;
import java.util.List;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

/**
 *Description of caching rule.
 * @author rssh
 */
public class CachingRuleDescription {

    @XmlType(name="RulePart")
    public static class Part
    {
        @XmlAttribute(name="cacheName")
        public String getCacheName()
        { return cacheName_; }

        public void setCacheName(String cacheName)
        { cacheName_=cacheName; }

        @XmlAttribute(name="action")
        public CacheAction getCacheAction()
        { return cacheAction_; }

        public void setCacheAction(CacheAction cacheAction)
        { cacheAction_=cacheAction; }

        private String cacheName_;
        private CacheAction cacheAction_;
    }

    @XmlElement(name="left")
    public Part getLeft()
    { return left_; }

    public void setLeft(Part left)
    { left_=left; }

    @XmlElement(name="right")
    public List<Part>  getRight()
    { return right_; }

    public void setRight(List<Part> right)
    {
        right_=right;
    }

    private Part left_;
    private List<Part> right_;

}
