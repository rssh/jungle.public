
package ua.gradsoft.jungle.persistence.ejbqlao;

import java.lang.reflect.Field;
import java.util.List;
import ua.gradsoft.jungle.persistence.ejbqlao.util.Pair;
import ua.gradsoft.jungle.persistence.ejbqlao.util.iso8601.DateParser;
import ua.gradsoft.jungle.persistence.ejbqlao.util.iso8601.InvalidDateException;

/**
 *Utility object which used for parsion 
 * @author rssh
 */
public class ObjectParser {
    
    
    public static Object parseListAsQueryParameter(List<?> list)
    {
        // TODO: parse dates in ISO-FORMAT
        if (list.size()==2) {
           Object frs = list.get(0);
           Object snd = list.get(1);
           if (frs instanceof String) {
               if (frs.equals("DATE")) {
                   if (snd instanceof String) {
                     try {  
                       java.sql.Date dt = new java.sql.Date(DateParser.parse((String)snd).getTime());
                       return dt;
                     }catch(InvalidDateException ex){
                         throw new ObjectParseException(ex.getMessage(),ex);
                     }
                   }else if (snd instanceof java.util.Date){
                     java.sql.Date dt = new  java.sql.Date(((java.util.Date)snd).getTime());
                     return dt;
                   }else{
                       throw new ObjectParseException("Can't transform "+snd.toString()+" to date");
                   }
               }else if (frs.equals("TIME")) {
                   if (snd instanceof java.sql.Time) {
                       return snd;
                   }else if (snd instanceof java.util.Date){
                       return new java.sql.Time(((java.util.Date)snd).getTime());
                   }else if (snd instanceof String) {
                       try {
                         java.sql.Time time = new java.sql.Time(DateParser.parse((String)snd).getTime());
                         return time;
                       }catch(InvalidDateException ex){
                           throw new ObjectParseException(ex.getMessage(),ex);  
                       }
                   }else{
                       throw new ObjectParseException("Can't transform "+snd.toString()+" to time");                       
                   }
               }else if (frs.equals("TIMESTAMP")) {
                   if (snd instanceof java.sql.Timestamp) {
                       return snd;
                   }else if (snd instanceof java.util.Date) {
                       return new java.sql.Timestamp(((java.util.Date)snd).getTime());
                   }else if (snd instanceof String) {
                     try{  
                       java.sql.Timestamp tms = new java.sql.Timestamp(DateParser.parse((String)snd).getTime());
                       return tms;
                     }catch(InvalidDateException ex){
                           throw new ObjectParseException(ex.getMessage(),ex);  
                     }                       
                   }else{
                        throw new ObjectParseException("Can't transform "+snd.toString()+" to time");                           
                   }
               }else{
                   return list;
               }               
           }else{
               return list;
           }
        }else{
          return list;
        }
    }
    
    public static int parseInt(Object value)
    {
        if (value instanceof String) {
          return Integer.parseInt((String)value);
        }else if (value instanceof Number) {
          Number n = (Number)value;
          return n.intValue();
        }else{
            throw new ObjectParseException("Can't transform "+value.toString()+"to int");
        }
    }

    public static boolean parseBoolean(Object value)
    {
       if (value instanceof Boolean) {
           return (Boolean)value;
       } else if (value instanceof Number) {
          Number n = (Number)value;
          return n.intValue()!=0;           
       } else if (value instanceof String) {
          String s = (String)value;
          if (s.equalsIgnoreCase("true")) {
              return true;
          }else if (s.equalsIgnoreCase("T")) {
              return true;
          }else if (s.equalsIgnoreCase("yes")){
              return true;
          }else if (s.equals("1")){
              return true;
          }else if (s.equalsIgnoreCase("false")){
              return false;
          }else if (s.equalsIgnoreCase("F")){
              return false;
          }else if (s.equalsIgnoreCase("no")){
              return false;
          }else if (s.equals("0")){
              return false;
          }else{
              throw new ObjectParseException("Can't transform string '"+s+"' to boolean");
          }
       } else {
           throw new ObjectParseException("Can't transform '"+value.toString()+"' to boolean");
       }
    }


    public static boolean isHibernateType(List<Object> list)
    {
     if (list.size()==3) {
         Object frs=list.get(0);
         if (frs instanceof String) {
             return frs.equals("HIBERNATE-TYPE");
         }else{
             return false;
         }
     }else{
         return false;
     }
    }

    public static Pair<Object,Object> getHibernateTypedParameter(List<Object> list) {
        Object snd=list.get(1);
        Object thr=list.add(2);
        Object hibernateType = null;
        Class hibernateTypeClass;
        try {
         hibernateTypeClass = Class.forName("org.hibernate.Type");
        }catch(ClassNotFoundException ex){
            throw new ObjectParseException("Can't parse HIBERBATE-TYPE",ex);
        }
        if (snd instanceof String) {
            String ssnd = ((String)snd);
            int lastDotPos = ssnd.lastIndexOf(".");
            if (lastDotPos > 0) {
              String className = ssnd.substring(0, lastDotPos);
              String constantName = ssnd.substring(lastDotPos);
              Class constantOwner;
              try {
                constantOwner = Class.forName(className);
              }catch(ClassNotFoundException ex){
                throw new ObjectParseException("Can't parse HIBERBATE-TYPE, constant class not found:"+className,ex);
              }
              Field f;
              try {
               f = constantOwner.getField(constantName);
              }catch(NoSuchFieldException ex){
                throw new ObjectParseException("Can't parse HIBERBATE-TYPE, field "+constantName+" in constant class "+className+" not found.",ex);
              }
              try {
                hibernateType = f.get(null);
              }catch(IllegalAccessException ex){
                throw new ObjectParseException("Can't parse HIBERBATE-TYPE, field "+constantName+" in constant class "+className+" is not accessible.",ex);
              }catch(NullPointerException ex){
                throw new ObjectParseException("Can't parse HIBERBATE-TYPE, field "+constantName+" in constant class "+className+" is not static.",ex);
              }
              if (!hibernateTypeClass.isAssignableFrom(hibernateType.getClass())) {
                  throw new ObjectParseException("argument of HIBERNATE-TYPE is not hibernateType, but "+hibernateType.getClass().getName());
              }
            }
        }else if (hibernateTypeClass.isAssignableFrom(snd.getClass())){
            hibernateType = snd;
        }else{
            throw new ObjectParseException("Can't parse HIBERNATE-TYPE, second argument "+snd.toString());
        }
        return new Pair<Object,Object>(hibernateType,thr);
    }

}
