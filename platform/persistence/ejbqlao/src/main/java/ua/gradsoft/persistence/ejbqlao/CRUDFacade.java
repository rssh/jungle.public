
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


    <T>       T     find(Class<T> tClass, Object id);
    
    @Caching(cacheName="queryCache", action=CacheAction.CACHE,
             keyBuilder=AllArguments.class)
    public <T> List<T> executeQuery(Class<T> tClass, String ejbql);

    @Caching(cacheName="queryCache", action=CacheAction.CACHE,
             keyBuilder=AllArguments.class)
    public <T> List<T> executeQuery(Class<T> tClass, String ejbql, List<Object> positionParameters);

   /**
    *Executre query with given options. 
    * @param tClass class of return value.
    * @param ejbql  query to select,
    * @param positionParameters  parameters to query, organized as position.
    * @param options qurty options, one of:
    * <ul>
    *  <li> maxResults </li>  number of results to retrieve
    *  <li> firstResult </li> index of firstResult in query
    *  <li> native </li> use natiove sql
    *  <li> nocache </li>  does not cache query.
    * </ul>
    *        
    * @return result of query evaluation.
    */
    @Caching(cacheName="queryCache", action=CacheAction.CACHE,
             keyBuilder=AllArguments.class, 
             policyInterceptor=InterceptCachingOptions.class)
    public <T> List<T> executeQuery(Class<T> tClass, String ejbql, List<Object> positionParameters, Map<String,Object> options);

    @Caching(cacheName="queryCache", action=CacheAction.CACHE,
             keyBuilder=AllArguments.class, 
             policyInterceptor=InterceptCachingOptions.class)
    public <T> List<T> executeQuery(Class<T> tClass, String ejbql, Map<String,Object> namedParameters, Map<String,Object> options);

    @Caching(cacheName="queryCache", action=CacheAction.CLEAR,
             keyBuilder=AllArguments.class,
             policyInterceptor=InterceptCachingOptions.class)
    public int executeUpdate(String ejbql, Map<String,Object> namedParameters, Map<String,Object> options);

    @Caching(cacheName="queryCache", action=CacheAction.CACHE, keyBuilder=AllArguments.class)
    public <T,C> List<T>  queryByCriteria(Class<T> tClass, C Criteria);


}
