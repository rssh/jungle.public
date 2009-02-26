package ua.gradsoft.jungle.configuration;

import java.math.BigDecimal;

public enum ConfigItemType
{
  STRING(1) {
      public Class<?>  getJavaClass()
      {
        return String.class;
      }
      public Object fromString(String s)
      {
        return s;
      }
      public String toString(Object o)
      {
        return o.toString();
      }
  },
  INTEGER(2){
      public Class<?>  getJavaClass()
      {
        return Integer.class;
      }
      public Object fromString(String s)
      {
        return Integer.parseInt(s);
      }
      public String toString(Object o)
      {
        return o.toString();
      }
  },
  BOOLEAN(3){
      public Class<?>  getJavaClass()
      {
        return Boolean.class;
      }
      public Object fromString(String s)
      {
        return Boolean.parseBoolean(s);
      }
      public String toString(Object o)
      {
        return o.toString();
      }
  },
  BIGDECIMAL(4){
      public Class<?>  getJavaClass()
      {
        return BigDecimal.class;
      }
      public Object fromString(String s)
      {
        return new BigDecimal(s);
      }
      public String toString(Object o)
      {
        return o.toString();
      }
  },
  DOUBLE(5){
      public Class<?>  getJavaClass()
      {
        return Double.class;
      }
      public Object fromString(String s)
      {
        return Double.parseDouble(s);
      }
      public String toString(Object o)
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

  public abstract Object fromString(String s);
  
  public abstract String toString(Object o);

  public int getCode()
  { return code_; }

  private int code_;
}

