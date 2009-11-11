
package ua.gradsoft.xwikinaming;

import com.xpn.xwiki.XWikiContext;
import com.xpn.xwiki.api.Api;
import java.util.Enumeration;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 *Api for XWIkiNamingAPI
 * @author rssh
 */
public class XWikiNamingPluginApi extends Api
{

    public XWikiNamingPluginApi(XWikiNamingPlugin plugin, XWikiContext context)
    {
      super(context);
      plugin_=plugin;
    }
    
    public Object lookup(String name)
    {
      try {
          return plugin_.lookup(name);
      }catch(NamingException ex){          
          LOG.debug("exception during lookup "+name,ex);
          lastException_=ex;
          return null;
      }  
    }
    
    public Enumeration list(String name)
    {
      try {
          return plugin_.list(name);
      }catch(NamingException ex){          
          LOG.debug("exception during list "+name,ex);
          lastException_=ex;
          return null;
      }          
    }
    
    public Exception getLastException()
    {
        return lastException_;
    }
    
    private static final Log LOG = LogFactory.getLog(XWikiNamingPluginApi.class);
    private XWikiNamingPlugin plugin_;
    private NamingException   lastException_=null; 
            
}
