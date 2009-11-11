package ua.gradsoft.xwikisql;

import ua.gradsoft.xwikisql.impl.InMemorySqlResult;
import com.xpn.xwiki.XWiki;
import com.xpn.xwiki.XWikiContext;
import com.xpn.xwiki.XWikiException;
import com.xpn.xwiki.api.Api;
import com.xpn.xwiki.plugin.XWikiDefaultPlugin;
import com.xpn.xwiki.plugin.XWikiPluginInterface;
import java.beans.XMLDecoder;
import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.math.BigDecimal;
import java.sql.Blob;
import java.sql.Clob;
import java.sql.Connection;
import java.sql.NClob;
import java.sql.PreparedStatement;
import java.sql.Ref;
import java.sql.ResultSet;
import java.sql.RowId;
import java.sql.SQLException;
import java.sql.SQLXML;
import java.sql.Time;
import java.sql.Timestamp;
import java.sql.Types;
import java.util.Date;
import java.util.List;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * XWiki sql plugin
 *@author Ruslan Shevchenko 
 * <a href="http://www.gradsoft.ua"> Grad-Soft Ltd. </a> 
 * &lt;Ruslan@Shevchenko.Kiev.UA&gt;
 **/
public class SqlPlugin extends XWikiDefaultPlugin 
        implements XWikiPluginInterface
        
{
  public SqlPlugin(String name, String className, XWikiContext context) {
     super(name,className,context);
  }

  /**
   * "sql"
   *@return sql
   **/
  @Override
  public String getName()
  { return "sql"; }

  @Override
  public Api  getPluginApi(XWikiPluginInterface plugin, XWikiContext context)
  { return new SqlPluginApi((SqlPlugin)plugin,context); }  

  /**
   * @deprecated
   */
  @Override
  public void flushCache()
  {}

  @Override
  public void init(XWikiContext context) //throws XWikiException
  {
     
     try { 
        super.init(context);
        initConfiguration(context);
     }catch(XWikiException ex){
         // unitl XWiki-2058 does not fixed.
         throw new RuntimeException(ex);
     }
  }

  SqlDatabase createDatabase(SqlPluginApi api, String name)
  {
    try {  
      DataSource ds = createDataSource(name); 
      LOG.debug("created database "+name);
      return new SqlDatabase(api,name,ds);
    }catch(NamingException ex){
      LOG.debug("NamingException during creation of database "+name,ex);  
      return new SqlDatabase(api,name,ex);  
    }
  }
  
  
  private DataSource createDataSource(String name) throws NamingException
  {
        DataSource datasource;
        Context jndiCntx = (Context) new InitialContext().lookup(configuration.getInitialContextPrefix());            
        datasource = (DataSource) jndiCntx.lookup(name);
        return datasource;
  }

  
  
  public void handleError(SqlErrorable source, String msg, String sql, Exception ex)
  {
    if (source.getLogEnabled()) {  
      if (msg!=null && sql!=null) {    
        if (ex!=null) {
          LOG.error(msg+" sql is ("+sql+")",ex);
        }else{
          LOG.error(msg+" sql is ("+sql+")",ex);  
        }      
      }else if (msg!=null && sql==null) {       
        if (ex!=null) {
           LOG.error(msg,ex);
        }else{
           LOG.error(msg); 
        }
      }else if (msg==null && sql!=null) {        
        // impossible ?
        
        LOG.error(" sql is ("+sql+")",ex);
      }else {
        LOG.error("exception",ex);
      }
    }
    SqlRuntimeException localEx = null;
    if (ex!=null) {
        if (msg!=null) {
          localEx = new SqlRuntimeException(msg,ex);
        }else{
          localEx = new SqlRuntimeException(ex);  
        }
    } else {
       localEx = new SqlRuntimeException(msg); 
    }    
    source.setLastException(ex);
    if (source.getThrowExceptions()) {        
        throw localEx;
    }
  }
  
  
  
  
  
  SqlResult  executeQueryUnwrapped(SqlDatabase database, String sql, List<Object> params) throws SQLException
  {
    if (configuration.getLogSql()) {
      if (database.getLogEnabled()) {  
        LOG.info("executeQuery:"+sql);
      }
    }  
    DataSource ds = database.getDataSource();
    Connection cn = ds.getConnection();    
    try {
      PreparedStatement st = cn.prepareStatement(sql);
      bindInputParameters(database,st,sql,params);
      ResultSet rs = st.executeQuery();
      InMemorySqlResult mrs = new InMemorySqlResult(database,rs);    
      return mrs;
    }finally{
       try{ 
        cn.close();
       }catch(SQLException ex){
          if (database.getLogEnabled()) { 
             LOG.warn("exception during closing connection:",ex);
          }
       }
    }
  }

  Integer  executeUpdateUnwrapped(SqlDatabase database, String sql, List<Object> params) throws SQLException
  {      
    if (configuration.getLogSql()) {
      if (database.getLogEnabled()) {  
        LOG.info("executeUpdate:"+sql);
      } 
    }   
    
    DataSource ds = database.getDataSource();
    Connection cn = ds.getConnection();    
    try{  
      PreparedStatement st = cn.prepareStatement(sql);
      bindInputParameters(database,st,sql,params);
      int retval = st.executeUpdate();
      return new Integer(retval);
    }finally{
       try { 
         cn.close();    
       }catch(SQLException ex){
         if (database.getLogEnabled()) {  
           LOG.warn("exception during closing connection:",ex);
         }
       }         
    }
  }
  
  
  
  void   bindInputParameters(SqlErrorable errorSource,
                             PreparedStatement st, 
                             String sql, 
                             List<Object> params) throws SQLException
  {
    for(int i=0; i<params.size(); ++i) {
        Object param = params.get(i);
        if (param == null) {
            st.setNull(i+1, Types.NULL);
       // } else if (param instanceof InMemorySqlArray) {
       //     throw new RuntimeException("Not implemented");
        } else if (param instanceof java.sql.Array) {
            st.setArray(i+1, (java.sql.Array)param);
        } else if (param instanceof InputStream) {
            st.setBlob(i+1, ((InputStream)param));
        } else if (param instanceof Blob) {
            st.setBlob(i+1, (Blob)param);
        } else if (param instanceof Boolean) {
            st.setBoolean(i+1,((Boolean)param).booleanValue() );        
        } else if (param instanceof BigDecimal) {
            st.setBigDecimal(i+1, (BigDecimal)param);
        } else if (param instanceof Byte) {
            st.setByte(i+1, ((Byte)param).byteValue());
        } else if (param instanceof Short) {
            st.setShort(i+1,((Short)param).shortValue());
        }else if (param instanceof Integer) {
            st.setInt(i+1, ((Integer)param).intValue() );
        }else if (param instanceof Long){
            st.setLong(i+1, (Long)param);
        }else if (param instanceof Clob){
            st.setClob(i+1, (Clob)param);
        }else if (param instanceof Reader) {
            st.setClob(i+1, (Reader)param);
        }else if (param instanceof Double){
            st.setDouble(i+1,(Double)param);
        }else if (param instanceof Float){
            st.setFloat(i+1, (Float)param);
        }else if (param instanceof NClob){
            st.setNClob(i+1, (NClob)param);       
        }else if (param instanceof Ref){
            st.setRef(i+1, (Ref)param);
        }else if (param instanceof RowId){
            st.setRowId(i+1, (RowId)param);
        }else if (param instanceof SQLXML ){
            st.setSQLXML(i+1, (SQLXML)param);     
        }else if (param instanceof String){
            st.setString(i+1, (String)param);   
        }else if (param instanceof Time){
            st.setTime(i+1, (Time)param);
        }else if (param instanceof Timestamp) {
            st.setTimestamp(i+1, (Timestamp)param);         
        }else if (param instanceof Date){         
            java.sql.Date xDate = new java.sql.Date(((Date)param).getTime());           
            st.setDate(i+1,xDate);            
        }else if (param instanceof List) {
            List pl = (List)param;
            if (pl.size()!=2) {
                handleError(errorSource,"typed bind must have arity 2",sql,null);
            }            
            String s = pl.get(0).toString();
            Object o = pl.get(1);
            int sqlType = SqlTypes.getTypeByName(s);
            if (sqlType==-1) {
                handleError(errorSource,"unknown type "+s,sql,null);
            }
            switch(sqlType) {
                case Types.DATE: 
                {                
                   Date x = SqlTypes.objectToDate(errorSource, o);
                   java.sql.Date xDate = new java.sql.Date(x.getTime());
                   st.setDate(i+1, xDate);
                   break;
                }
                case Types.TIME:
                {
                    Date x = SqlTypes.objectToDate(errorSource, o);
                    st.setTime(i+1, new Time(x.getTime()));
                    break;
                }
                case Types.TIMESTAMP:
                {
                    Date x = SqlTypes.objectToDate(errorSource, o);
                    st.setTimestamp(i+1, new Timestamp(x.getTime()));
                    break;
                }
                case Types.NULL:
                {
                    st.setNull(i+1, Types.NULL);
                    break;
                }
                default:
                {
                    // for now no more custom syntax
                    errorSource.handleError("no custom syntax for type "+s, sql, null);
                }                
            }
        }else{
            st.setObject(i, param);
        }
    }
  }
  
  
  public  SqlPluginConfiguration getConfiguration()
  {
    return configuration;  
  }
  
  void initConfiguration(XWikiContext context) throws XWikiException
  {
    String fname = null;  
    XWiki wiki = context.getWiki();
    if (wiki!=null) {
      fname = wiki.Param("xwikisql.configuration.fname");
      if (fname!=null) {
        InputStream inputStream = null;
        try {
            inputStream=new BufferedInputStream(new FileInputStream(fname));
        }catch(IOException ex){
            throw new XWikiException(
                    XWikiException.MODULE_XWIKI_PLUGINS,
                    XWikiException.ERROR_XWIKI_INIT_FAILED,
                    "can't open "+fname, ex);
        }
        XMLDecoder xmlDecoder = new XMLDecoder(inputStream);
        xmlDecoder.setOwner(this);
        Object o = xmlDecoder.readObject();
        if (! (o instanceof SqlPluginConfiguration)) {
            throw new XWikiException(
                    XWikiException.MODULE_XWIKI_PLUGINS,
                    XWikiException.ERROR_XWIKI_INIT_FAILED,                    
                    "incorrect object in config file: must be ua.gradsoft.xwikisql.SqlPluginConfiguration "
                    );
        }
        configuration = (SqlPluginConfiguration)o;     
      }else{
        configuration = new SqlPluginConfiguration();
      }      
    
      int maxRows = (int)wiki.ParamAsLong("xwikisql.maxRowsRetrieved", configuration.getMaxRowsRetrieved());
      if (maxRows != configuration.getMaxRowsRetrieved()) {
        configuration.setMaxRowsRetrieved(maxRows);
      }
    
      boolean throwExceptions = (wiki.ParamAsLong("xwkisql.throwsExceptions",configuration.getThrowExceptions()?1:0))!=0;
      if (throwExceptions!=configuration.getThrowExceptions()) {
        configuration.setThrowExceptions(throwExceptions);
      }
      
      boolean nullAsEmptyString = (wiki.ParamAsLong("xwikisql.nullAsEmptyString",configuration.getNullAsEmptyString()?1:0))!=0;
      if (nullAsEmptyString!=configuration.getNullAsEmptyString()) {
          configuration.setNullAsEmptyString(nullAsEmptyString);
      }
      
      String initialContextPrefix = wiki.Param("xwikisql.initialContextPrefix");
      if (initialContextPrefix!=null) {
          configuration.setInitalContextPrefix(initialContextPrefix);
      }
      
      boolean logSql = wiki.ParamAsLong("xwikisql.logSql",configuration.getLogSql()?1:0)!=0;
      if (logSql!=configuration.getLogSql()) {
          configuration.setLogSql(logSql);
      }
      
      boolean logEnabled = wiki.ParamAsLong("xwikisql.logEnabled",configuration.getLogEnabled()?1:0)!=0;
      if (logEnabled!=configuration.getLogEnabled()) {
          configuration.setLogEnabled(logEnabled);
      }
      
      
    }else{
        //test model
        LOG.debug("init default configuration in test mode");
        configuration = new SqlPluginConfiguration();
    }
  }
  
  private static SqlPluginConfiguration configuration=null;    
  private static final Log LOG = LogFactory.getLog(SqlPlugin.class);
  

}

