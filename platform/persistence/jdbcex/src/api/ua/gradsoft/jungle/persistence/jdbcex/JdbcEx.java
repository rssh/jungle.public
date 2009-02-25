package ua.gradsoft.jungle.persistence.jdbcex;

import java.sql.Connection;

/**
 * DB-specifics extensions to JDBC interface.
 **/ 
public abstract class JdbcEx
{
  
 /**
 * get dialect (the same as name of configuration, i.e. pgsql, hsql, oracle ...)
 **/
 public abstract String getDialect();

 /**
  * true, when database supports schemas.
  **/
 public abstract boolean isSupportMultipleSchemas();

 /**
 * true if databse supports offset and limit in select statements.
 **/ 
 public abstract boolean isSupportsOffsetAndLimit();
 
 /**
  * get next sequence number for string with name <code> name </code>
  **/
 public abstract long getNextSequenceNumber(String name, Connection cn);

 /**
  * create sequence with name <code> name </code> and value 
  * <code> initialValue </code>. Note, that in most implementation this is 
  * DDL statement, which must live in own transaction. 
  **/ 
  public abstract void createSequence(String name, long initial, int increment, Connection cn);


 /**
  * drop sequence with name <code> name</code>.
  * Note, that in most implementation this is 
  * DDL statement, which must live in own transaction. 
  **/ 
  public abstract void dropSequence(String name, Connection cn);

  
  /**
   * return API for work with database extensions, if one is
   * configured, otherwse throws IllegalStateException. 
   **/
  public static  JdbcEx getInstance()
  {
    if (instance_==null) {
      throw new IllegalStateException(
                   "JdbcEx singleton is not configurated");
    }
    return instance_;
  }

  public static void setInstance(JdbcEx instance)
  {
    instance_=instance;
  }

  protected void checkValidSqlIdentifier(String name)
  {
    if (!name.matches("([a-z]|[A-Z]|_)([a-z]|[A-Z]|[0-9]|_)*")) {
        throw new IllegalArgumentException("name '"+name+"' is not valid sql exception");
    }
  }


  private static JdbcEx instance_;

}
