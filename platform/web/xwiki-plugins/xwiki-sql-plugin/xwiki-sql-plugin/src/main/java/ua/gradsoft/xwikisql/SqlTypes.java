/*
 */

package ua.gradsoft.xwikisql;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Types;
import java.util.Date;
import java.util.HashMap;
import ua.gradsoft.xwikisql.imported.jigsaw.DateParser;
import ua.gradsoft.xwikisql.imported.jigsaw.InvalidDateException;

/**
 *static class for work with sql types.
 * @author rssh
 */
public class SqlTypes {
    
    public static String typeName(int type)
    {
        switch(type) {
            case Types.ARRAY:
                return "ARRAY";
            case Types.BIGINT:
                return "BIGINT";
            case Types.BINARY:
                return "BINARY";
            case Types.BIT:
                return "BIT";
            case Types.BLOB:
                return "BLOB";
            case Types.BOOLEAN:
                return "BOOLEAN";
            case Types.CHAR:
                return "CHAR";
            case Types.CLOB:
                return "CLOB";
            case Types.DATALINK:
                return "DATALINK";
            case Types.DATE:
                return "DATE";
            case Types.DECIMAL:
                return "DECIMAL";
            case Types.DISTINCT:
                return "DISTINCT";
            case Types.DOUBLE:
                return "DOUBLE";
            case Types.FLOAT:
                return "FLOAT";
            case Types.INTEGER:
                return "INTEGER";
            case Types.JAVA_OBJECT:
                return "JAVA_OBJECT";
            case Types.LONGNVARCHAR:
                return "LONGNVARCHAR";
            case Types.LONGVARBINARY:
                return "LONGVARBINARY";
            case Types.LONGVARCHAR:
                return "LONGVARCHAR";
            case Types.NCHAR:
                return "NCHAR";
            case Types.NCLOB:
                return "NCLOB";
            case Types.NULL:
                return "NULL";
            case Types.NUMERIC:
                return "NUMERIC";
            case Types.NVARCHAR:
                return "NVARCHAR";
            case Types.OTHER:
                return "OTHER";
            case Types.REAL:
                return "REAL";
            case Types.REF:
                return "REF";
            case Types.ROWID:
                return "ROWID";
            case Types.SMALLINT:
                return "SMALLINT";
            case Types.SQLXML:
                return "SQLXML";
            case Types.STRUCT:
                return "STRUCT";
            case Types.TIME:
                return "TIME";
            case Types.TIMESTAMP:
                return "TIMESTAMP";
            case Types.TINYINT:
                return "TINYINT";
            case Types.VARBINARY:
                return "VARBINARY";
            case Types.VARCHAR:
                return "VARCHAR";
            default:
                return "UNKNOWN";
        }
    }

    public static int getTypeByName(String name)
    {
        String upp = name.toUpperCase();
        Integer retval = typeNames.get(upp);
        if (retval==null) {
            return -1;
        }else{
            return retval.intValue();
        }
    }
    
    
    public static Date objectToDate(SqlErrorable errorSource, Object o)
    {
      if (o==null) {
          return null;
      } else if (o instanceof Date) {
          return (Date)o;
      } else if (o instanceof String) {
          //ISO8601
          try {
            return DateParser.parse((String)o);
          }catch(InvalidDateException ex){
              transformationError(errorSource, o.getClass(),"String",ex.getMessage());
              return null;
          }
      }else if (o instanceof Long) {
          // when we think. that this is milliseconds,
          return new Date(((Long)o).longValue());
      }else{
          transformationError(errorSource, o.getClass(),"Date");
          return null;
      }
    }
    
    public static Boolean objectToBoolean(SqlErrorable errorable, Object o)
    {
      if (o==null) {
          return null;
      } else if (o instanceof Boolean) {
          return ((Boolean)o).booleanValue();
      }else if (o instanceof Number) {
          //!=0
          return new Boolean(((Number)o).intValue()!=0);
      }else if (o instanceof Character) {
          char ch = ((Character)o).charValue();
          if (ch=='1'||ch=='y'||ch=='Y'||ch=='t'||ch=='T') {
              return true;
          }else if (ch=='0'||ch=='n'||ch=='N'||ch=='f'||ch=='F'){
              return false;
          }else{
              transformationError(errorable, Character.class,"Boolean","ch="+ch);
              return null;
          }
      }else if (o instanceof String) {
          String s = (String)o;
          if (s.length()==1) {
              return objectToBoolean(errorable,s.charAt(0));
          }else if (s.length()==0) {
              return false;
          }else if (s.equalsIgnoreCase("true")||s.equalsIgnoreCase("yes")||s.equals("1")) {
              return true;
          }else if (s.equalsIgnoreCase("false")||s.equalsIgnoreCase("no")||s.equals("0")) {
              return false;
          }else{
              transformationError(errorable,String.class,"Boolean","s="+s);
              return null;
          }
      }else{
          transformationError(errorable,o.getClass(),"Boolean");
          return null;
      }
    }
    
    public static Double objectToDouble(SqlErrorable errorable, Object o)
    {
      if (o==null) {
          return null;
      } else if (o instanceof Double) {
          return (Double)o;
      }else if (o instanceof Number) {
          Number n = (Number)o;
          return n.doubleValue();
      }else if (o instanceof String) {
          try {
              return Double.parseDouble((String)o);
          }catch(NumberFormatException ex){
              transformationError(errorable, o.getClass(),"Double","s="+(String)o);
              return null;
          }
      }else{
          transformationError(errorable, o.getClass(),"Double");
          return null;
      }
    }
    
    public static BigDecimal objectToBigDecimal(SqlErrorable errorable, Object o)
    {
      if (o==null) {
          return null;
      } else if (o instanceof BigDecimal) {
          return (BigDecimal)o;
      }else if (o instanceof Number) {
          Number n = (Number)o;
          if (n instanceof BigInteger){
              return new BigDecimal((BigInteger)n);
          }else if (n instanceof Double) {
              return new BigDecimal(n.doubleValue());
          }else if (n instanceof Float){
              return new BigDecimal(n.doubleValue());
          }else{
              // integer types.          
              return new BigDecimal(n.longValue());
          }
      }else if (o instanceof String) {
          try {
              return new BigDecimal((String)o);
          }catch(NumberFormatException ex){
              transformationError(errorable, o.getClass(),"BigDecimal","s="+(String)o);
              return null;
          }
      }else{
          transformationError(errorable, o.getClass(),"BigDecimal");
          return null;
      }
    }
    
    public static Long objectToLong(SqlErrorable errorable, Object o)
    {
      if (o==null) {
          return null;
      } else if (o instanceof Long) {
          return (Long)o;
      }else if (o instanceof Number) {
          return ((Number)o).longValue();
      }else if (o instanceof String) {
          try {
              return Long.parseLong((String)o);
          }catch(NumberFormatException ex){
              transformationError(errorable, o.getClass(),"Long","s="+(String)o);
              return null;
          }
      }else{
          transformationError(errorable, o.getClass(),"Long");
          return null;
      }
    }
   
    public static Integer  objectToInteger(SqlErrorable errorable, Object o)
    {
       if (o==null) {
           return null;
       }else if (o instanceof Integer){
           return (Integer)o;
       }else if (o instanceof Number){
           return ((Number)o).intValue();
       }else if (o instanceof String){
           try {
               return Integer.parseInt((String)o);
           }catch(NumberFormatException ex){
              transformationError(errorable, o.getClass(),"Integer","s="+(String)o);
              return null;               
           }
       }else{
          transformationError(errorable, o.getClass(),"Integer");
          return null;           
       }
    }
    
    public static String objectToString(SqlPlugin plugin, SqlErrorable errorable, Object o)
    {
       if (o==null) { 
           if (plugin.getConfiguration().getNullAsEmptyString()) {
               return "";
           }else{
               return null;
           }
       } else if (o instanceof String) {
           return (String)o;
       }else if (o instanceof Date) {
           // data - mapped as ISO8601 string.
           return DateParser.getIsoDateNoMillis((Date)o);
       }else{
           return o.toString();
       }
    }
    
    
    
    private static void transformationError(SqlErrorable errorable, Class fromClass, String to)
    {
      transformationError(errorable,fromClass,to,null);    
    }

    private static void transformationError(SqlErrorable errorable, Class fromClass, String to, String additional)
    {
       String message = "can't transform "+fromClass.getName()+" to "+to;
       if (additional!=null) {
           message=message+"("+additional+")";
       }
       errorable.handleError(message, null, null); 
    }
    
    
    private static HashMap<String,Integer> typeNames;
    static {
      typeNames=new HashMap<String,Integer>();  
      typeNames.put("ARRAY", Types.ARRAY);
      typeNames.put("BIGINT", Types.BIGINT);
      typeNames.put("BINARY", Types.BINARY);
      typeNames.put("BIT", Types.BIT);
      typeNames.put("BLOB", Types.BLOB);
      typeNames.put("BOOLEAN", Types.BOOLEAN);
      typeNames.put("CHAR", Types.CHAR);
      typeNames.put("CLOB", Types.CLOB);
      typeNames.put("DATALINK", Types.DATALINK);
      typeNames.put("DATE", Types.DATE);
      typeNames.put("DECIMAL", Types.DECIMAL);
      typeNames.put("DISTINCT", Types.DISTINCT);
      typeNames.put("DOUBLE",Types.DOUBLE);
      typeNames.put("FLOAT", Types.FLOAT);
      typeNames.put("INTEGER", Types.INTEGER);
      typeNames.put("JAVA_OBJECT", Types.JAVA_OBJECT);
      typeNames.put("LONGNVARCHAR", Types.LONGNVARCHAR);
      typeNames.put("LONGVARBINARY", Types.LONGVARBINARY);
      typeNames.put("LONGVARCHAR", Types.LONGVARCHAR);
      typeNames.put("NCHAR", Types.NCHAR);
      typeNames.put("NCLOB", Types.NCLOB);
      typeNames.put("NULL", Types.NULL);
      typeNames.put("NUMERIC", Types.NUMERIC);
      typeNames.put("NVARCHAR", Types.NVARCHAR);
      typeNames.put("OTHER", Types.OTHER);
      typeNames.put("REAL", Types.REAL);
      typeNames.put("REF", Types.REF);
      typeNames.put("ROWID",Types.ROWID);
      typeNames.put("SMALLINT",Types.SMALLINT);
      typeNames.put("SQLXML",Types.SQLXML);
      typeNames.put("STRUCT",Types.STRUCT);
      typeNames.put("TIME", Types.TIME);      
      typeNames.put("TIMESTAMP", Types.TIMESTAMP);
      typeNames.put("TINYINT", Types.TINYINT);
      typeNames.put("VARBINARY", Types.VARBINARY);
      typeNames.put("VARCHAR", Types.VARCHAR);      
    }  
    

    
}
