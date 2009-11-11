/*
 * part of XWiki sql plugin
 *(C) Ruslan Shevchenko <Ruslan@Shevchenko.Kiev.UA>
 * 2008
 */

package ua.gradsoft.xwikisql.impl;

import ua.gradsoft.xwikisql.*;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.TreeMap;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;


/**
 * SQLResult, which holds all in memory
 */
public class InMemorySqlResult implements SqlResult
{
    
    public InMemorySqlResult(SqlDatabase theDatabase, ResultSet rs) throws SQLException
    {
      init(theDatabase,rs);  
    }

    private void init(SqlDatabase theDatabase, ResultSet rs) throws SQLException
    {        
      plugin=theDatabase.getSqlPluginApi().getSqlPlugin();
      throwExceptions=theDatabase.getThrowExceptions();
      logEnabled=theDatabase.getLogEnabled();
      nColumns = rs.getMetaData().getColumnCount();
      indexesByNames = new TreeMap<String,Integer>();
      namesByIndexes = new String[nColumns];
      typesByIndexes = new int[nColumns];
      for(int i=0; i<nColumns; ++i) {
          String name = rs.getMetaData().getColumnLabel(i+1).toUpperCase();
          indexesByNames.put(name, i);
          namesByIndexes[i]=name;
          int type = rs.getMetaData().getColumnType(i+1);
          typesByIndexes[i]=type;
      }
      nRows=0;
      values=new ArrayList<Object[]>();
      notAllRows=false;
      while(rs.next()) {
          Object[] row = new Object[nColumns];
          for(int i=0; i<nColumns; ++i) {
              row[i] = rs.getObject(i+1);
          }       
          values.add(row);
          ++nRows;
          if (nRows >= plugin.getConfiguration().getMaxRowsRetrieved()) {
              notAllRows=true;
              break;
          }
      }            
    }
    
    // Enumeration interface
    
    public boolean hasMoreElements()
    {
      return currentRow < nRows;  
    }
    
    public SqlResultRow nextElement()
    {
      if (currentRow < nRows) {
        return new InMemorySqlResultRow(this,currentRow++);    
      } else {
        return null;  
      }    
    }

    public Exception handleError(String msg, String sql, Exception ex)
    {
      plugin.handleError(this, msg,sql,ex);
      return sqlRuntimeException;
    }
    

    public boolean wasError()
    {
       return sqlRuntimeException!=null; 
    }
    
    public Exception getLastException()
    {
       return sqlRuntimeException;  
    }
    
    public void setLastException(Exception ex){
       sqlRuntimeException=ex; 
    }
    
    public void clearError()
    {
      sqlRuntimeException = null;
    }
    
    public boolean getThrowExceptions()
    {
      return throwExceptions;  
    }
    
    public void setThrowExceptions(boolean value)
    {
      throwExceptions=value;  
    }

    public boolean getLogEnabled()
    {
      return logEnabled;  
    }
    
    public void setLogEnabled(boolean value)
    {
      logEnabled=value;  
    }
    
    
    public boolean isNotAllRows()
    { return notAllRows; }
    
    public int getNRows()
    { return nRows; }
    
    public int getNColumns()
    { return nColumns; }
    
    public boolean hasColumnName(String name)
    {
      return getColumnIndex(name.toUpperCase())!=-1;  
    }
    
    public String getColumnName(int index)
    {
      if (index>=nColumns) {
          handleError("invalid column index "+index, null, null);
          return null;
      }else{
          return namesByIndexes[index];
      }  
    }
    
    public int getColumnTypeAsInt(String name)
    {
       return getColumnTypeAsInt(getColumnIndex(name));
    }
    

    public int getColumnTypeAsInt(int index)
    {
       if (index==-1 || index >= nColumns) {
           return Types.NULL;           
       }else{
           return typesByIndexes[index];
       }
    }
    
    
    public String getColumnType(String name)
    {
       return SqlTypes.typeName(getColumnTypeAsInt(name));                  
    }
    
    
    public String getColumnType(int index)
    {
       return SqlTypes.typeName(getColumnTypeAsInt(index));                  
    }
    
    
    public int getColumnIndex(String name)
    {
       Integer io = indexesByNames.get(name.toUpperCase());
       return io==null ? -1 : io.intValue();
    }

    
    SqlPlugin  getPlugin()
    {
        return plugin;
    }
    
    
    Object[]  getRawRow(int index)
    { return values.get(index); }
    
    
    private int nColumns;
    private int nRows;
    private boolean notAllRows;
    private ArrayList<Object[]> values;   
    private TreeMap<String,Integer>  indexesByNames;
    private String[] namesByIndexes;
    private int[]    typesByIndexes;
        
    // enumerator functionality
    private int currentRow=0;
    
    // SQLErrorable functionality
    private Exception sqlRuntimeException=null;
    private boolean   throwExceptions;
    private boolean   logEnabled;
    
    // plugin access.
    private SqlPlugin plugin;
    
    private static final Log LOG = LogFactory.getLog(InMemorySqlResult.class);
    
}
