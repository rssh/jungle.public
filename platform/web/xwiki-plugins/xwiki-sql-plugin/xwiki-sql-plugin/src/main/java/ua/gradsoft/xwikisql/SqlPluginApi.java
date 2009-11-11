package ua.gradsoft.xwikisql;

import com.xpn.xwiki.XWikiContext;
import com.xpn.xwiki.api.Api;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;



/**
 * API Object
 **/
public class SqlPluginApi extends Api
{

  public SqlPluginApi(SqlPlugin thePlugin,XWikiContext context)
  {
    super(context);  
    plugin=thePlugin;  
    if (context==null) {
        test=true;
    }else if (context.getWiki()==null) {
        test=true;
    }
  }
  
  /**
   * get database object.
   *@param dbname -- jndi name of datasource, configured in container.
   **/
  public SqlDatabase  getDatabase(String dbname)
  {
      if (test||hasProgrammingRights()) {       
        return plugin.createDatabase(this, dbname);
      }else{
        if (plugin.getConfiguration().getLogEnabled()) {  
          LOG.info("user "+context.getUser()+" has not programming rights");  
        }
        return null;  
      }
  }
  
  /**
   * give access to implemtation class of sql plugin.
   * do not use this function from velocity: public is implementation effect.
   */
  public SqlPlugin  getSqlPlugin()
  {
    if (test||hasProgrammingRights()) {
        return plugin;
    }else{
        if (plugin.getConfiguration().getLogEnabled()) {
          LOG.info("user "+context.getUser()+" has not programming rights");  
        }
        return null;          
    }  
  }
  
  private SqlPlugin plugin;
  private boolean   test=false;

  private static final Log LOG = LogFactory.getLog(SqlPluginApi.class);

}
