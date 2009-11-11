package ua.gradsoft.jungle.configuration;

import java.io.Serializable;
import java.math.BigDecimal;

public enum ConfigItemType implements Serializable
{
  STRING(1) {
      public Class<?>  getJavaClass()
      {
        return String.class;
      }
      public Serializable fromString(String s)
      {
        return s;
      }
      public String toString(Serializable o)
      {
        return o.toString();
      }
  },
  INTEGER(2){
      public Class<?>  getJavaClass()
      {
        return Integer.class;
      }
      public Serializable fromString(String s)
      {
        return Integer.parseInt(s);
      }
      public String toString(Serializable o)
      {
        return o.toString();
      }
  },
  BOOLEAN(3){
      public Class<?>  getJavaClass()
      {
        return Boolean.class;
      }
      public Serializable fromString(String s)
      {
        return Boolean.parseBoolean(s);
      }
      public String toString(Serializable o)
      {
        return o.toString();
      }
  },
  BIGDECIMAL(4){
      public Class<?>  getJavaClass()
      {
        return BigDecimal.class;
      }
      public Serializable fromString(String s)
      {
        return new BigDecimal(s);
      }
      public String toString(Serializable o)
      {
        return o.toString();
      }
  },
  DOUBLE(5){
      public Class<?>  getJavaClass()
      {
        return Double.class;
      }
      public Serializable fromString(String s)
      {
        return Double.parseDouble(s);
      }
      public String toString(Serializable o)
      {
        return o.toString();
      }
  }
          ;

  ConfigItemType(int code)
  {
    code_=code;
  }

  public abstract Class<?>  getJavaClass();

  public abstract Serializable fromString(String s);
  
  public abstract String toString(Serializable o);

  public int getCode()
  { return code_; }

  private int code_;
}

