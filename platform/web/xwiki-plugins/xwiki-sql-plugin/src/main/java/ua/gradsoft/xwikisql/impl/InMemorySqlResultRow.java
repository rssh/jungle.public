/*
 * part of sql plugin for xwiki.
 * (C) GradSoft Ltd, Kiev, Ukraine.
 * (C) Ruslan Shevchenko.
 * http://www.gradsoft.ua 
 */

package ua.gradsoft.xwikisql.impl;

import ua.gradsoft.xwikisql.*;
import ua.gradsoft.xwikisql.impl.InMemorySqlResult;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import ua.gradsoft.xwikisql.imported.jigsaw.DateParser;
import ua.gradsoft.xwikisql.imported.jigsaw.InvalidDateException;

/**
 *SQLResultRow in memory.
 * @see SqlResultRow
 */
public class InMemorySqlResultRow implements SqlResultRow
{

    InMemorySqlResultRow(InMemorySqlResult theOwner, int theRowIndex)
    {
       owner = theOwner;
       rowIndex = theRowIndex;
       row = theOwner.getRawRow(rowIndex);
    }
    
    
    public Object getObject(String name)
    {
       int i = owner.getColumnIndex(name); 
       if (i!=-1) {
           return row[i];
       }else{
           owner.handleError("Invalid column name:"+name, null, null);
           return null;
       }       
    }
    
    public Object getObject(int i)
    {
      if (i<row.length) {
          return row[i];
      }else{
          owner.handleError("Invalid column index:"+i, null, null);
          return null;
      }        
    }
       
    
    public boolean isNull(String name)
    {
      return getObject(name)==null;  
    }

    public boolean isNull(int index)
    {
      return getObject(index)==null;  
    }
    
    public String getString(String name)
    {
      return SqlTypes.objectToString(owner.getPlugin(), owner, getObject(name));  
    }
        
    public String getString(int index)
    {
         return SqlTypes.objectToString(owner.getPlugin(), owner, getObject(index));  
    }    

    
    public Long getLong(String name)
    {
       return SqlTypes.objectToLong(owner, getObject(name));  
    }
        
    public Long getLong(int index)
    {
       return SqlTypes.objectToLong(owner, getObject(index));  
    }    
    
    public Integer getInteger(String name)
    {
       return SqlTypes.objectToInteger(owner, getObject(name));  
    }
        
    public Integer getInteger(int index)
    {
       return SqlTypes.objectToInteger(owner, getObject(index));  
    }    
    
    
    public BigDecimal getBigDecimal(String name)
    {
       return SqlTypes.objectToBigDecimal(owner,getObject(name));  
    }
        
    public BigDecimal getBigDecimal(int index)
    {
       return SqlTypes.objectToBigDecimal(owner,getObject(index));  
    }    
    
    
    public Double getDouble(String name)
    {
       return SqlTypes.objectToDouble(owner,getObject(name));  
    }
        
    public Double getDouble(int index)
    {
       return SqlTypes.objectToDouble(owner,getObject(index));  
    }    
    
    
    public Boolean getBoolean(String name)
    {
       return SqlTypes.objectToBoolean(owner,getObject(name));  
    }
        
    public Boolean getBoolean(int index)
    {
       return SqlTypes.objectToBoolean(owner,getObject(index));  
    }    
    
    public Date getDate(String name)
    {
       return SqlTypes.objectToDate(owner,getObject(name));  
    }
        
    public Date getDate(int index)
    {
       return SqlTypes.objectToDate(owner,getObject(index));  
    }           
    
    
    private InMemorySqlResult  owner;
    private int      rowIndex;
    private Object[] row;
    
    private static final Log LOG = LogFactory.getLog(InMemorySqlResult.class);
    
    
}
