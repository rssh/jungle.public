
package ua.gradsoft.persistence.ejbqlao;

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
    *  <li> jdbcnative </li> use native sql via underlaying jdbc call.
    *  <li> nocache </li>  does not cache query.
    *  <li> resultSetMapping </li> use resultSetMapping
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
    * @param namedParameters  named parameters to query.
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
     *@see List ua.gradsoft.persistence.ejbqlao.CRUDFacade#queryByCriteria(Class, C, Map>)
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
     * Update with command. Cache is cleaned.
     * @param <C> - class of command
     * @param Command - update command
     * @return number of affected rows or 0.
     */
    @Caching(cacheName="queryCache", action=CacheAction.CLEAR)
    public <C> int  updateWithCommand(C Command);



}
