package ua.gradsoft.xwikinaming;

import com.xpn.xwiki.XWikiContext;
import com.xpn.xwiki.api.Api;
import com.xpn.xwiki.plugin.XWikiDefaultPlugin;
import com.xpn.xwiki.plugin.XWikiPluginInterface;
import java.util.Enumeration;
import javax.naming.Context;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;


/**
 *Plugin, which integrate xwiki with external
 * naming system, such as jndi or spring.
 * @author rssh
 */
public class XWikiNamingPlugin extends XWikiDefaultPlugin
{

    public XWikiNamingPlugin(String name, String classname, XWikiContext context)
    {
      super(name,classname,context);
      init(context);
    }
    
    @Override
    public void init(XWikiContext context)
    {
      super.init(context);  
      Configuration cf = new Configuration();
      cf.init(context.getWiki());      
      namingProvider_ = new JNDINamingProvider();
      try {
        namingProvider_.init(cf);
      }catch(NamingException ex){
        LOG.fatal("Can't init naming:",ex);        
        throw new RuntimeException("Can't init naming:",ex);
      }catch(Exception ex){
        LOG.fatal("Can't init naming:",ex);        
        throw new RuntimeException("Can't init naming:",ex);
      }
      initialContext_ = namingProvider_.getContext();
    }
    
    public Api getPluginApi(XWikiPluginInterface plugin,XWikiContext context)
    {
      return new XWikiNamingPluginApi((XWikiNamingPlugin)plugin,context);  
    }
    
    public void flushCache()
    {}        
    
    /**           
     * @return "naming"
     */
    public String getName()
    {
      return "naming";  
    }
    
    public Object lookup(String name) throws NamingException
    {
       return initialContext_.lookup(name);
    }

    public Enumeration list(String name) throws NamingException
    {
        NamingEnumeration e = initialContext_.list(name);
        return e;
    }
        
    
    private static final Log LOG = LogFactory.getLog(XWikiNamingPlugin.class);
    private NamingProvider namingProvider_;
    private Context initialContext_;
    private boolean  wasError_=false;
    private Exception lastException_=null;
    
}
