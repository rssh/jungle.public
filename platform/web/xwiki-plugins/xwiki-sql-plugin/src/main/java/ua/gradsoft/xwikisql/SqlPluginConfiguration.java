/*
 */

package ua.gradsoft.xwikisql;

import java.io.Serializable;

/**
 *Object, which holds configuration information.
 * Can be readed/stored from JaxB XML.
 * @author rssh
 */
public class SqlPluginConfiguration implements Serializable
{

    /**    
     * get maximum number of rows, which can be retrieved in one 
     * sql query.  
     *@see SqlDatabase#executeQuery(java.lang.String,java.util.List)
     */
    public int getMaxRowsRetrieved()
    { return maxRetrievedRows; }
    
    /**    
     *@see SqlPluginConfiguration#getMaxRowsRetrieved()
     */
    public void setMaxRowsRetrieved(int value)
    { maxRetrievedRows=value; }
    
    /**
     * get policy of error handling.
     * When this property is set to true, than behaviour of
     * library on error is immediatly throw appropriative exception.
     * When false -- than library call return some special value
     * (usially null) and set fields in associated SqlErrorable object.
     *@see SqlErrorable
     */
    public boolean getThrowExceptions()
    {
      return throwExceptions;  
    }

    /**
     *set throwExceptions property 
     *@see SqlPluginConfiguration#getThrowExceptions
     */
    public void setThrowExceptions(boolean value)
    {
      throwExceptions=value;  
    }
    
    /**
     *when property is enabled, log executed sql statements.
     */
    public boolean getLogSql()
    {
      return logSql;  
    }

    /**
     *set logSql property 
     *@see SqlPluginConfiguration#getLogSql
     */
    public void setLogSql(boolean value)
    {
      logSql=value;  
    }
    

    /**
     *when property is enabled, SqlResultRow.getString(String columnName) and
     *SqlResultRow.getString(int columnIndex) methods return empty string instead
     *null, when appropriative column value is null. 
     *@see ua.gradsoft.xwikisql.SqlResultRow#getString(String) 
     *@see ua.gradsoft.xwikisql.SqlResultRow#getString(int)  
     */
    public boolean getNullAsEmptyString()
    {
      return nullAsEmptyString;  
    }

    /**
     * set nullAsEmptyString property to value <code> value </code>
     * @see SqlPluginConfiguration#getNullAsEmptyString()
     */
    public void setNullAsEmptyString(boolean value)
    {
       nullAsEmptyString=value; 
    }
    
    /**
     * get initialContextPrefix property.  
     * This is prefix of JNDI initial context, where we search name of
     * datasource. 
     * Default -- <code> java:comp/env </code>, can be
     * changed by appropriateve setter of configurable variable 
     * <code> xwikisql.initialContextPrefix </code>
     *@see ua.gradsoft.xwikisql.SqlPluginApi#getDatabase
     */
    public String getInitialContextPrefix()
    { return initialContextPrefix; }
    
    /**
     * set initialContextPrefix property to value <code> value </code>
     * @see SqlPluginConfiguration#getInitialContextPrefix
     */    
    public void setInitalContextPrefix(String value)
    { initialContextPrefix=value; }
    
    /**
     *when property is enabled (by default) do logging according
     * with settings of xwiki log system.
     */
    public boolean getLogEnabled()
    {
      return logEnabled;  
    }

    /**
     *set logEnabled property 
     *@see SqlPluginConfiguration#getLogEnabled
     */
    public void setLogEnabled(boolean value)
    {
      logEnabled=value;  
    }
    
    
    private int maxRetrievedRows = 1000;
    
    private boolean throwExceptions = true;
    
    private boolean logSql = false;
    
    private boolean logEnabled = true;
    
    private boolean nullAsEmptyString = true;
    
    private String initialContextPrefix = "java:comp/env";
    
}
