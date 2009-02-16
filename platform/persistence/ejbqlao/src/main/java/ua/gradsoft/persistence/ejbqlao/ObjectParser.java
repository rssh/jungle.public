
package ua.gradsoft.persistence.ejbqlao;

import java.util.List;
import ua.gradsoft.persistence.ejbqlao.util.iso8601.DateParser;
import ua.gradsoft.persistence.ejbqlao.util.iso8601.InvalidDateException;

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

}
