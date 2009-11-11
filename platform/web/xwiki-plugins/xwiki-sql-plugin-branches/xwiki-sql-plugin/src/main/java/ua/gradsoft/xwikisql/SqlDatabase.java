package ua.gradsoft.xwikisql;

import java.sql.SQLException;
import java.sql.Connection;
import java.util.Collections;
import java.util.List;
import javax.sql.DataSource;

/**
 * Object, which holds name of connection pool and can 
 * evaluate sql statements on it.
 **/
public class SqlDatabase implements SqlErrorable
{


 SqlDatabase(SqlPluginApi theApi, String theDbName, DataSource theDataSource)
 {
   api=theApi;
   //dbName=theDbName;
   dataSource=theDataSource;
   lastException=null;
   SqlPlugin plugin = theApi.getSqlPlugin();
   throwExceptions=plugin.getConfiguration().getThrowExceptions();
   logEnabled=plugin.getConfiguration().getLogEnabled();
 }
 
 SqlDatabase(SqlPluginApi theApi, String theDbName, Exception ex)
 {
   api=theApi;
   //dbName=theDbName;
   dataSource=null;
   lastException=ex;
   SqlPlugin plugin = theApi.getSqlPlugin();
   throwExceptions=plugin.getConfiguration().getThrowExceptions();
   logEnabled=plugin.getConfiguration().getLogEnabled();   
   handleError("exception during creation of datasource "+theDbName,null,ex);
 }

 /**
  * execute sql query <code> sql </code>.
  * Shortcat to <code> executeQuery(sql,Collections.emptyList()) </code>
  *@return result of query wrapped to SqlResult object.
  *@see SqlDatabase#executeQuery(java.lang.String,java.util.List)
  **/
 public SqlResult  executeQuery(String sql)
 {
   return executeQuery(sql,Collections.<Object>emptyList());
 }
 

 /**
  * execute sql query <code> sql </code> with parameters <code> params <code>.
  * <p>
  * Database cursor is closed immediatly after query execution (Data is copied
  * into memory if necossary), so user can not think about connection
  * management.
  * </p>
  * <p>
  * If query is return more than maxRetrievedRows configuration value, then
  * only <code> maxRetrievedRows </code> rows actually retrieved and call of
  * isNotAllRows() of returned SqlResult value must return true.
  * </p>
  * <p>
  * If error is occured during execution of query, than behavour is depend
  * from throwExceptions configuration variable: when one is set to true, than
  * SqlRuntimeException is thrown, otherwise null is returned and SqlErrorable
  * wasError and lastException fields are set.
  * </p>
  *
  *@param sql - sql query to execute
  *@param params - sql binden variables for this query.
  *@return result of query wrapped to SqlResult.
  *@exception SqlRuntimeExceptin
  *
  *@see SqlResult
  *@see SqlPluginConfiguration#getThrowExceptions
  *@see SqlPluginConfiguration#getMaxRowsRetrieved
  *@see java.sql.Statement#executeQuery
  **/
 public SqlResult  executeQuery(String sql,List<Object> params)
 {
  if (dataSource==null) {
      this.handleError("DataSource is null, check saved exception ", sql, lastException);
      return null;
  }   
  try {   
   return api.getSqlPlugin().executeQueryUnwrapped(this,sql,params);
  }catch(SQLException ex){
      this.handleError("SQLException occured", sql, ex);
      return null;
  }
 }
 
 /**
  * execute sql statement <code> sql </code>.
  * Shortcat to <code> executeQuery(sql,Collections.emptyList()) </code>,
  *@see SqlDatabase#executeUpdate(java.lang.String,java.util.List)
  **/
 public Integer executeUpdate(String sql)
 {
   return executeUpdate(sql,Collections.<Object>emptyList());
 }
 
 /**
  * execute sql statement <code> sql </code> with parameters 
  *  <code> params <code>.
  * Statement can be INSERT, UPDATE, DELETE or DDL statement.
  * <code>
  * <p>
  * Database cursor is closed immediatly after query execution. 
  * </p><p>
  * If error is occured during execution of query, than behavour is depend
  * from throwExceptions configuration variable.
  * </p>
  *@exception SqlRuntimeExceptin
  *@see SqlPluginConfiguration#getThrowExceptions
  *@see java.sql.Statement#executeUpdate
  **/
 public Integer  executeUpdate(String sql,List<Object> params)
 {
  if (dataSource==null) {
      return null;
  }   
  try {   
   return api.getSqlPlugin().executeUpdateUnwrapped(this,sql,params);
  }catch(SQLException ex){
      this.handleError("SQLException occured", sql, ex);
      return null;
  }
 }
  
 
 public Exception handleError(String msg, String sql, Exception ex)
 {
   api.getSqlPlugin().handleError(this, msg, sql, ex);  
   return lastException;
 }

  public boolean wasError()
  {
   return lastException!=null;   
  }

  public Exception getLastException()
  {
    return lastException;  
  }

  public void setLastException(Exception ex)
  {
    lastException = ex;  
  }
  
  public void clearError()
  {
    lastException=null;  
  }
  
  public boolean getThrowExceptions()
  {
    return throwExceptions;  
  }
  
  public void setThrowExceptions(boolean value)
  { throwExceptions=value; }
  
  public boolean getLogEnabled()
  {
    return logEnabled;  
  }
  
  public void setLogEnabled(boolean value)
  { logEnabled=value; }
  
  
   /**
    * direct access to native Java connection.
    **/
   public Connection  getNativeConnection() throws SQLException
   {
    return dataSource.getConnection();
   }

   DataSource  getDataSource()
   { return dataSource; }
   
   /**
    * @return api
    */
   public SqlPluginApi      getSqlPluginApi()
   {  
     return api;
   }
  
   private SqlPluginApi  api;
   //private String        dbName;
   private DataSource    dataSource;
   private Exception     lastException;
   private boolean       throwExceptions;
   private boolean       logEnabled;
  

}
