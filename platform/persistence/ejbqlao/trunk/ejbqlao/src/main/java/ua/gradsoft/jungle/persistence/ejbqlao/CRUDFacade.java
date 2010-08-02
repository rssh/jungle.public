
package ua.gradsoft.jungle.persistence.ejbqlao;

import java.util.List;
import java.util.Map;
import ua.gradsoft.caching.CacheAction;
import ua.gradsoft.caching.annotations.Caching;
import ua.gradsoft.caching.util.AllArguments;


/**
 *Facade for CRUD operations
 * @author rssh
 */
public interface CRUDFacade 
{

    /**
     * create new entity and store one with db.
     * @param newEntity - created entity.
     */
    @Caching(cacheName="queryCache", action=CacheAction.CLEAR)
    <T>       void  create(T newEntity);

    @Caching(cacheName="queryCache", action=CacheAction.CLEAR)
    <T>       void  update(T newEntity);

    @Caching(cacheName="queryCache", action=CacheAction.CLEAR)
    public void  remove(Object id);

    @Caching(cacheName="queryCache", action=CacheAction.CACHE,
             keyBuilder=AllArguments.class)
    <T>       T     find(Class<T> tClass, Object id);
    
    @Caching(cacheName="queryCache", action=CacheAction.CACHE,
             keyBuilder=AllArguments.class)
    public <T> List<T> executeQuery(Class<T> tClass, String ejbql);

   /**
    *Execute query with given options. 
    * @param tClass class of return value.
    * @param ejbql  query to select,
    * @param positionParameters  positions parameters to query.
    * @param options querty options, one of:
    * <ul>
    *  <li> maxResults </li>  number of results to retrieve
    *  <li> firstResult </li> index of firstResult in query
    *  <li> native </li> use native sql
    *  <li> jdbcnative </li> use jdbc-native sql
    *  <li> nocache </li>  does not cache query.
    *  <li> resultSetMapping </li> use resultSetMapping
    *  <li> enable-filter </li>  enable filter with name option value
    *  <li> enable-filters </li> enable all filters with names from option value (which must be list)
    *  <li> disable-filter </li> disable filter with name as option value.
    *  <li> disable-filters </li> disable all filters with names from option value (which must be list)
    * </ul>
    *        
    * @return result of query evaluation.
    */
    @Caching(cacheName="queryCache", action=CacheAction.CACHE,
             keyBuilder=AllArguments.class, 
             policyInterceptor=InterceptCachingOptions.class)
    public <T> List<T> executeQuery(Class<T> tClass, String ejbql, List<Object> positionParameters, Map<String,Object> options);

   /**
    *Execute query with given options. If this method is called in cache wrapper, that
    * result is stored in queryCache
    * @param tClass class of return value.
    * @param ejbql  query to select,
    * @param namedParameters  named parameters to query.  If named parameters have form 'x.y' (i. e. with dot) and
    * underlaying JPA implementation supports filters functionality, than this parameter is interpreted
    * as parameter of filter 'x' (with name 'y') and appropriative filter is enabled in current session.
    * Also exists special forms, when parameter is represented as list or array such as
    * <ul>
    *  <li>
    *    ["DATE", value] -- pass value as Date
    *  </li>
    *  <li>
    *    ["TIME", value] -- pass value as Time
    *  </li>
    *  <li>
    *    ["TIMESTAMP", value] -- pass value as Timestamp
    *  </li>
    *  <li>
    *    ["HIBERNATE-TYPE", type, value] -- pass value with help of hibernate-specific
    *   query setParemeter(String,Object,org.hibernate.type.Type)
    *  </li>
    * </ul>
    * @param options querty options
    *
    * @return result of query evaluation.
    */
    @Caching(cacheName="queryCache", action=CacheAction.CACHE,
             keyBuilder=AllArguments.class, 
             policyInterceptor=InterceptCachingOptions.class)
    public <T> List<T> executeQuery(Class<T> tClass, String ejbql, Map<String,Object> namedParameters, Map<String,Object> options);

    @Caching(cacheName="queryCache", action=CacheAction.CLEAR,
             keyBuilder=AllArguments.class,
             policyInterceptor=InterceptCachingOptions.class)
    public int executeUpdate(String ejbql, Map<String,Object> namedParameters, Map<String,Object> options);

    /**
     * query by criteria.
     *@see List ua.gradsoft.persistence.ejbqlao.CRUDFacade#queryByCriteria(Class, C, Map)
     **/
    @Caching(cacheName="queryCache", action=CacheAction.CACHE, keyBuilder=AllArguments.class)
    public <T,C> List<T>  queryByCriteria(Class<T> tClass, C Criteria);


    /**
     * Query by critera.
     *  Criteria - is a special object, which is transformed to query and set of parameters
     * by criteria helper. Criteria is any class, for which exists CriteriaHelper.  And CriteriaHelper
     * for class <code>Xxx</code> is just class with name <code>XxxCriteriaHelper</code>, which
     * implements CriteriaHelper interface.
     *@see ua.gradsoft.persistence.ejbqlao.CriteriaHelper
     **/
    @Caching(cacheName="queryCache", action=CacheAction.CACHE, keyBuilder=AllArguments.class)
    public <T,C> List<T>  queryByCriteria(Class<T> tClass, C Criteria, Map<String, Object> options);

    /**
     * query count for items, selected by given criteria.
     **/
    @Caching(cacheName="queryCache", action=CacheAction.CACHE, keyBuilder=AllArguments.class)
    public <T extends Number, C> T  queryCountByCriteria(Class<T> tClass, C criteria);

    /**
     * Update with command. Cache is cleaned.
     * Command id a special object, which is transformet to update query and set of parameters by command helper.
     * @param <C> - class of command
     * @param Command - update command
     * @return number of affected rows or 0.
     *@see ua.gradsoft.persistence.ejbqlao.CommandHelper
     */
    @Caching(cacheName="queryCache", action=CacheAction.CLEAR)
    public <C> int  updateWithCommand(C Command);


  /**
   * Generate next id for class entityClass, if some field in entityClass POJO
   * definition was marked as SequenceKey
   * @param <T> - type of key
   * @param <E> - type of POJP entity.
   * @param entityClass
   * @param idClass
   * @return next sequence or null, if such sequence does not exists.
   */
   public<T,E> T generateNextSequenceKey(Class<E> entityClass, Class<T> idClass);


}
