package ua.gradsoft.jungle.persistence.jpaex;

import javax.persistence.EntityManager;
import ua.gradsoft.jungle.persistence.jpaex.impl.DefaultJdbcWorkExecutor;



/**
 * Extentions to JPA, which have no standard api (when most implementation 
 * supports nearly equal features)
 **/ 
public abstract class JpaEx
{
  /**
   * get JdbcConnectionWrapper with jdbc connection, which situated in
   *same transaction as entity manager. 
   */
  public abstract JdbcConnectionWrapper  getJdbcConnectionWrapper(EntityManager em, boolean readOnly);

  /**
   * get API for execute JdbcWork 
   */
  public JdbcWorkExecutor  getJdbcWorkExecutor(EntityManager em)
  {
    return new DefaultJdbcWorkExecutor(getJdbcConnectionWrapper(em,false));
  }


  /**
   * register this instance of JpaEx as singleton.
   * (used during system initialization)
   */
  public void registerInstance()
  {
      JpaEx.setInstance(this);
  }

  /**
   * get instance of JpaEx, if it is configured in application, otherwise
   * throws IllegalStateException
   **/ 
  public static JpaEx  getInstance()
  {
   if (instance_==null) {
     lazyInitInstance();
     if (instance_==null) {
       throw new IllegalStateException("JpaEx singleton is not configured");
     }
   }
   return instance_;
  }

  /**
   * return true if JpaEx singleton is configured (i. e. JpaEx.getInstance()
   *  can be called).
   */ 
  public static boolean isConfigured()
  {
   if (instance_==null) {
     lazyInitInstance();
   }
   return (instance_!=null);
  }

  /**
   * set Instance.
   *  (this method is used for configuring JpaEx from IOC container 
   *  configuration)
   **/ 
  public static void setInstance(JpaEx instance)
  { instance_=instance; }

  private static void lazyInitInstance()
  {
   // do nothing for now.
  }

  private static JpaEx instance_;
}

