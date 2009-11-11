package ua.gradsoft.caching;

import javax.xml.bind.annotation.adapters.XmlAdapter;

/**
 *JAXB adapter for class elements
 * @author rssh
 */
public class XmlClassAdapter extends XmlAdapter<String,Class>
{

    public String marshal(Class clazz)
    {
       return clazz.getName();
    }

    public Class unmarshal(String className) throws Exception
    {
        return Class.forName(className);
    }


}
