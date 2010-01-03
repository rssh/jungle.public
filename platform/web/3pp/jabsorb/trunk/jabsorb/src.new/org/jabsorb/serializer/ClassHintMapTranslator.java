package org.jabsorb.serializer;

import java.util.HashMap;
import java.util.Map;

/**
 *Simple translator, which allows set mapping.
 * @author ruslan@shevchenko.kiev.ua
 */
public class ClassHintMapTranslator implements ClassHintTranslator
{

    private Map _map = new HashMap();

    public Map  getMap()
    { return _map; }

    public void  setMap(Map map)
    { _map = map; }

    public void  put(Class from, Class to)
    { _map.put(from,to); }

    public Class translate(Class clazz) 
    {
      return (Class)_map.get(clazz);  
    }


}
