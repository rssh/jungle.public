package ua.gradsoft.jungle.persistence.jpaex;



import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.sql.Connection;
import javax.persistence.EntityManager;
import ua.gradsoft.jungle.persistence.jdbcex.JdbcEx;
import ua.gradsoft.jungle.persistence.jpaex.impl.DefaultJdbcWorkExecutor;
import ua.gradsoft.jungle.persistence.jpaex.impl.JpaEntityCloner;



/**
 * Extentions to JPA, which have no standard api (when most implementation 
 * supports nearly equal features)
 **/ 
public abstract class JpaEx
{


 /**
  * get dialect (the same as name of configuration, i.e. hibernate, toplink ...)
  **/
  public abstract String getDialect();


  /**
   * @param em - entity manager.
   * @return true if JPA implementation support creation of jdbc connections.
   */
  public abstract boolean isJdbcConnectionWrapperSupported(EntityManager em);

  /**
   * get JdbcConnectionWrapper with jdbc connection, which situated in
   *same transaction as entity manager. 
   */
  public abstract JdbcConnectionWrapper  getJdbcConnectionWrapper(EntityManager em, boolean readOnly);

  /**
   * return true if exists API for submitting JdbcWork to execution.  
   * @param em
   * @return
   */
  public boolean isJdbcWorkExecutorSupported(EntityManager em)
  {
      return isJdbcConnectionWrapperSupported(em);
  }

  /**
   * get API for execute JdbcWork 
   */
  public JdbcWorkExecutor  getJdbcWorkExecutor(EntityManager em)
  {
    return new DefaultJdbcWorkExecutor(getJdbcConnectionWrapper(em,false));
  }


  /**
   * get Jdbc dialect, if one is available, otherwise return null.
   */
  public abstract JdbcEx getJdbcEx();

  /**
   * get next sequence number for string with name <code> name </code>
   **/
   public long getNextSequenceNumber(String name, EntityManager em)
   {
     if (isJdbcConnectionWrapperSupported(em)) {
         JdbcConnectionWrapper wr = getJdbcConnectionWrapper(em, false);
         Connection cn = wr.getConnection();
         try {
           return getJdbcEx().getNextSequenceNumber(name, cn);
         }finally{
           wr.releaseConnection(cn);
         }
     }else{
         throw new UnsupportedOperationException();
     }
   }

 /**
  * create sequence with name <code> name </code> and value
  * <code> initialValue </code>. Note, that in most implementation this is
  * DDL statement, which must live in own transaction.
  **/
  public void createSequence(String name, long initial, int increment, EntityManager em)
  {
     if (isJdbcConnectionWrapperSupported(em)) {
       JdbcEx jdbcEx = getJdbcEx();
       JdbcConnectionWrapper wr = this.getJdbcConnectionWrapper(em, false);
       Connection cn = wr.getConnection();
       try {
         jdbcEx.createSequence(name, initial, increment, cn);
       }finally{
           wr.releaseConnection(cn);
       }
     }else{
       throw new UnsupportedOperationException();
     }
  }


 /**
  * drop sequence with name <code> name</code>.
  * Note, that in most implementation this is
  * DDL statement, which must live in own transaction.
  **/
  public void dropSequence(String name, EntityManager em)
  {
     if (isJdbcConnectionWrapperSupported(em)) {
       JdbcEx jdbcEx = getJdbcEx();
       JdbcConnectionWrapper wr = this.getJdbcConnectionWrapper(em, false);
       Connection cn = wr.getConnection();
       try {
         jdbcEx.dropSequence(name, cn);
       }finally{
           wr.releaseConnection(cn);
       }
     }else{
       throw new UnsupportedOperationException();
     }
  }

  /**
   * true, if this JPA implementation have vendor-specific method
   * for detach of beans, otherwise - false. In this method return true,
   * than
   */
  public boolean hasVendorSpecificDetached()
  {
    return false;
  }

    /**
     * detach bean from entity object, perform instantiations of
     * lazy-loaded parts, if needed.  By default use algorith,
     * defined in JPAEntityCloner (but can be overriden in vendor-specific
     *  subclasses)
     * @param em - enityManager fron which we want detach object/
     * @param bean - object to detach.
     * @return detached object.
     */
    public <T> T detached(EntityManager em, T bean)
    {
      return (T)JpaEntityCloner.unjpaObject(Object.class, bean, null);
    }


  /**
   * is functionality, equals to hibernate filters supported ?
   * @return true if fuctionality is supported
   */
  public abstract boolean isFiltersSupported();


  /**
   * Check - if filter exists and enabled.
   * @param entityManage - entityManager
   * @param filterName - name of filter
   * @return  if filter exists and enabled ?
   */
  public abstract boolean isFilterEnabled(EntityManager entityManager, String filterName);

  /**
   * enabled or disable filter.
   * @param entityManage - entityManager
   * @param filterName - name of filter
   * @param enabled - enable or disable.
   * @return  if filter exists and enabled ?
   */
  public abstract void setFilterEnabled(EntityManager entityManager, String filterName, boolean enabled);

  /**
   * set filter parameter to name 'name'. If filter was not enabled - enable one.
   * @param entityManage - entityManager
   * @param filterName - name of filter.
   * @param paramName - name of parameter.
   * @param param - value of parameter.
   */
  public abstract void setFilterParameter(EntityManager entityManager, String filterName, String paramName, Object param);




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

