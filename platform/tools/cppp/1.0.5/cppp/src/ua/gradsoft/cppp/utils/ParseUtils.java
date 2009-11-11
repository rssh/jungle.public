
package ua.gradsoft.cppp.utils;

import java.util.*;

public class ParseUtils
{

 /*
  * extract string from string token
  * (i. e. "xxx" -> xxx) 
  */
 public static String token2String(String s)
 {
  return s.substring(1,s.length()-1);
 }

 public static String charToken2String(String s)
 {
  return new Character(charToken2Char(s)).toString();
 }

 public static char charToken2Char(String s)
 {                   
  return s.substring(1,s.length()-1).charAt(0);
 }

 public static int  countLines(String s)
 {
  StringTokenizer tokenizer=new StringTokenizer(s,"\n");
  return tokenizer.countTokens();
 }

 // TODO: check for last symbol.
 public static int  parseInt(String s) throws ParseIntegerFormatException
 {                                     
  try {
   if (s.length() >= 2) {
     if (s.charAt(0)=='0') {
       if (s.charAt(1)=='x'||s.charAt(1)=='X') {
          return Integer.parseInt(s.substring(2),16);
       }else if(s.charAt(1)=='9'||s.charAt(1)=='8') {
          return Integer.parseInt(s);
       }else{
          return Integer.parseInt(s,8);
       }
     }else if(s.charAt(0)=='-' && s.charAt(1)=='0') {
       if (s.length() <3) {
         return 0;
       }else{
         if (s.charAt(2)=='x') {
           return - Integer.parseInt(s.substring(3),16);
         }else if(s.charAt(2)=='8'||s.charAt(2)=='9') {
           return Integer.parseInt(s);
         }else {
           return Integer.parseInt(s,8);
         }
       }
     }
   }  
   return Integer.parseInt(s);
  }catch(NumberFormatException ex){
    throw new ParseIntegerFormatException(ex.getMessage());
  }
 }                                                     

 // TODO: check for last symbol.
 public static int  parseHexadecimalInt(String s) throws ParseIntegerFormatException
 {
  try {
    if (s.length()>2) {
     if (s.charAt(0)!='-') { 
       return Integer.parseInt(s.substring(2),16);
     }else if(s.length()>3) {
       return - Integer.parseInt(s.substring(3),16);
     }else{
       // -0x ; impossible
       return 0;
     }
    }else{
      // 0x ; impossible
      return 0;
    }
  }catch(NumberFormatException ex){
    throw new ParseIntegerFormatException(ex.getMessage());
  }
 }

 // TODO: check for last symbol.
 public static int  parseDecimalInt(String s) throws ParseIntegerFormatException
 {
  try {
    return Integer.parseInt(s);
  }catch(NumberFormatException ex){
    throw new ParseIntegerFormatException(ex.getMessage());
  }
 }

 // TODO: check for last symbol.
 public static int  parseOctalInt(String s) throws ParseIntegerFormatException
 {
  try {
    return Integer.parseInt(s,8);
  }catch(NumberFormatException ex){
    throw new ParseIntegerFormatException(ex.getMessage());
  }
 }


 // TODO: check
 public static double parseDouble(String s) throws ParseDoubleFormatException
 {
  try {
    return Double.parseDouble(s);
  }catch(NumberFormatException ex){
    throw new ParseDoubleFormatException(ex.getMessage());
  }
 }


};
