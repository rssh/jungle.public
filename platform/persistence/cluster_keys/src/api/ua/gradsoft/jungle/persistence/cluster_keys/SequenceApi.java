package ua.gradsoft.jungle.persistence.cluster_keys;

import java.sql.Connection;

/**
 * Implementation-depended API for work with database sequences.
 **/ 
public abstract class SequenceApi
{

  /**
   * get next sequence number for string with name 'name'
   **/
  public abstract long getNextSequenceNumber(String name, Connection cn);

  /**
   * create sequence with name <code> name </code> and value 
   * <code> initialValue </code>. Note, that in most implementation this is 
   * DDL statement, which must live in own transaction. 
   **/ 
  public abstract void createSequence(String name, Long initial, Connection cn);


  /**
   * drop sequence with name <code> name</code>.
   * Note, that in most implementation this is 
   * DDL statement, which must live in own transaction. 
   **/ 
  public abstract void dropSequence(String name, Connection cn);


  /**
   * return database dialect. (same as name of configuration in ivy file)
   */
  public abstract String getDatabaseDialect();
  
  /**
   * return API for work with database sequences, if one is
   * configured, otherwse throws IllegalStateException. 
   **/
  public static  SequenceApi getInstance()
  {
    if (instance_==null) {
      throw new IllegalStateException(
                   "SequenceApi singleton is not configurated");
    }
    return instance_;
  }

  public static void setInstance(SequenceApi instance)
  {
    instance_=instance;
  }



  private static SequenceApi instance_;
};
