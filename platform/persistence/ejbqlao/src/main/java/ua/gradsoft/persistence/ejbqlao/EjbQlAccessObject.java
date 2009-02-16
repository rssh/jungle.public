package ua.gradsoft.persistence.ejbqlao;

import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.persistence.TemporalType;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Object, which erncapsulate remote entity manager.
 *(Generic implementation of CRUDFacade)
 **/
public abstract class EjbQlAccessObject implements CRUDFacade
{       
    
    public <T>       void  create(T newEntity)
    {
        getEntityManager().persist(newEntity);
    }
    
    public <T>       void  update(T newEntity)
    {
        getEntityManager().merge(newEntity);
    }
    
    public void  remove(Object id)
    {
        getEntityManager().remove(id);
    }
    
    public <T>       T     find(Class<T> tClass, Object id)
    {
        return getEntityManager().find(tClass, id);
    }
    
    
  public <T> List<T> executeQuery(Class<T> tClass, String ejbql)
  {
   return executeQuery(tClass,ejbql,Collections.<Object>emptyList(),Collections.<String,Object>emptyMap());   
  }
    
  public <T> List<T> executeQuery(Class<T> tClass, String ejbql, List<Object> positionParameters)
  {
    return executeQuery(tClass, ejbql,positionParameters,Collections.<String,Object>emptyMap());
  }

  public <T> List<T> executeQuery(Class<T> tClass, String ejbql, List<Object> positionParameters, Map<String,Object> options)
  {
   Query q = createQuery(ejbql, options);
   int i=1;
   for(Object param:positionParameters) {
       if (param instanceof Date) {
           q.setParameter(i, (Date)param, TemporalType.TIMESTAMP);
       }else if (param instanceof List) {
           List<Object> list = (List<Object>)param;
           q.setParameter(i, ObjectParser.parseListAsQueryParameter(list));
       }else if (param.getClass().isArray()) {
           Object[] array = (Object[])param;
           q.setParameter(i, ObjectParser.parseListAsQueryParameter(Arrays.asList(array)));
       }else{
           q.setParameter(i, param);
       }
       ++i;
   }
   applyOptions(q,options);
   List<T> retval = q.getResultList();
   return retval;
  }

  public <T> List<T> executeQuery(Class<T> tClass, String ejbql, Map<String,Object> namedParameters, Map<String,Object> options)
  {
     Query q = createQuery(ejbql, options);
     applyNamedParameters(q,namedParameters);
     applyOptions(q,options);
     List<T> retval = q.getResultList();
     return retval;     
  }

  public int executeUpdate(String ejbql, Map<String,Object> namedParameters, Map<String,Object> options)
  {
    Query q = createQuery(ejbql, options);
    applyNamedParameters(q, namedParameters);
    applyOptions(q,options);
    return q.executeUpdate();
  }

  public <T,C> List<T>  queryByCriteria(Class<T> tClass, C criteria)
  {
      Class criteriaHelperClass = findCriteriaHelperClass(criteria.getClass());
      if (criteriaHelperClass == null) {
          throw new IllegalArgumentException("Invalid criteria: can't find criteria helper for class "+criteria.getClass().getName());
      }
      Object o = null;
      try {        
        o = criteriaHelperClass.newInstance();
      } catch (InstantiationException ex) {
          throw new IllegalArgumentException("Can't instantiate class "+criteriaHelperClass.getName(),ex);
      } catch (IllegalAccessException ex){
          throw new IllegalArgumentException("Can't instantiate class "+criteriaHelperClass.getName(),ex);
      }
      if (!(o instanceof CriteriaHelper)) {
          throw new IllegalArgumentException("bad criteria helper for this criteria "+criteriaHelperClass.getClass().getName());
      }
      CriteriaHelper criteriaHelper = (CriteriaHelper)o;
      QueryParams params = criteriaHelper.getQueryParams(criteria);
      String query = params.getQuery();
      Map<String,Object> namedParameters = params.getNamedParameters();
      Map<String,Object> options = params.getOptions();
      return executeQuery(tClass, query, namedParameters, options);
  }
  
  private Query createQuery(String ejbql, Map<String, Object> options)
  {
      boolean isNative=false;
      Class resultClass = null;
      String resultSetMapping = null;
      if (options!=null) {
          Object o = options.get("native");
          if (o!=null) {
            if (o instanceof Boolean) {
                isNative=(Boolean)o;
            } else if (o instanceof Number) {
                isNative=((Number)o).intValue()!=0;
            } else if (o instanceof String) {
                String so = (String)o;
                isNative = (so.equalsIgnoreCase("true") || so.equals("1"));
            } else {
                throw new IllegalArgumentException("Non-boolean value for query option native :"+o.toString());
            }
          } 
          o = options.get("resultClass");
          if (o!=null) {
             if (o instanceof Class) {
                 resultClass = (Class)o;
             } else if (o instanceof String) {
                 try {
                   resultClass = Class.forName((String)o);
                 } catch (ClassNotFoundException ex){
                     throw new TypeNotPresentException((String)o,ex);
                 }
             } else {
                 throw new IllegalArgumentException("bad value for query option resultClass :"+o.toString());
             }
          }
          o = options.get("resultSetMapping");
          if (o!=null) {
              if (o instanceof String) {
                  resultSetMapping = (String)o;
              } else {
                  throw new IllegalArgumentException("bad value for query option resultSetMapping :"+o.toString());
              }
          }
      }
      if (isNative) {
         if (resultClass!=null) {
             return getEntityManager().createNativeQuery(ejbql, resultClass);
         }else if (resultSetMapping!=null) {
             return getEntityManager().createNativeQuery(ejbql, resultSetMapping);
         }else{
             return  getEntityManager().createNativeQuery(ejbql);          
         }            
      } else {
          return getEntityManager().createQuery(ejbql);
      }
  }

  private void applyNamedParameters(Query query, Map<String, ?> params)
  {
    for(Map.Entry<String,?> param: params.entrySet()) {
        applyNamedParameter(query,param);
    } 
  }
  
  private void applyNamedParameter(Query query, Map.Entry<String,?> param)
  {
         String name = param.getKey();
         Object value = param.getValue();
         if (value instanceof Date) {
           query.setParameter(name, (Date)value, TemporalType.TIMESTAMP);
         }else if (value instanceof List) {
           List<Object> list = (List<Object>)value;
           query.setParameter(name, ObjectParser.parseListAsQueryParameter(list));
       }else if (value.getClass().isArray()) {
           Object[] array = (Object[])value;
           query.setParameter(name, ObjectParser.parseListAsQueryParameter(Arrays.asList(array)));
       }else{
           query.setParameter(name, value);
       }               
  }
  
  private void applyOptions(Query query, Map<String,?> options)
  {
    for(Map.Entry<String,?> option: options.entrySet())  {
        applyOption(query,option);
    }
  }
  
  private void applyOption(Query query, Map.Entry<String,?> option)
  {
    String key = option.getKey();
    OptionParser evaluator = queryOptions.get(key);
    if (evaluator==null) {
        throw new IllegalArgumentException("Invalid option:"+key);
    }
    evaluator.apply(query, option.getValue());
  }
  
  public static interface OptionParser
  {
      void apply(Query q, Object o);

      public static class NothingOptionParser implements OptionParser
      {
        public void apply(Query q, Object o) {}
      }
      
      public static final NothingOptionParser NOTHING = new NothingOptionParser();
  }
  

  private static HashMap<String,OptionParser> initQueryOptions()
  {
      HashMap<String,OptionParser> a = new HashMap<String,OptionParser>();
      a.put("maxResults", new OptionParser()
      {
         public void apply(Query q,Object o)  {
             q.setMaxResults(ObjectParser.parseInt(o));             
         }
      }
              );
      a.put("firstResult", new OptionParser()
      {
         public void apply(Query q,Object o)  {
             q.setFirstResult(ObjectParser.parseInt(o));                    
         }
      }              
              );
      a.put("native", OptionParser.NOTHING);
      a.put("resultClass", OptionParser.NOTHING);
      a.put("resultSetMapping", OptionParser.NOTHING);
      a.put("nocache", OptionParser.NOTHING);
      return a;  
  }

  private Class findCriteriaHelperClass(Class criteriaClass)
  {
    if (criteriaClass==null ||
        criteriaClass.equals(Object.class) ||
        criteriaClass.equals(Enum.class)) {
        return null;
    }
    Class retval=null;
    String criteriaHelperClassName = criteriaClass.getName()+"CriteriaHelper";
    try {
        retval = Class.forName(criteriaHelperClassName);
    } catch (ClassNotFoundException ex) {
        if (LOG.isDebugEnabled()) {
          LOG.debug("Can't find class "+criteriaHelperClassName);
        }
    }
    if (retval!=null) {
        return retval;
    }
    retval = findCriteriaHelperClass(criteriaClass.getSuperclass());
    if (retval!=null) {
        return retval;
    }
    Class[] interfaces = criteriaClass.getInterfaces();
    for(int i=0; i<interfaces.length; ++i) {
        retval = findCriteriaHelperClass(interfaces[i]);
        if (retval!=null) {
            return retval;
        }
    }
    return retval;
  }

  /**   
   * allcessible only from local.
   * Must be overloaded from subclasses.
   * @return entityManager
   */
  public abstract EntityManager getEntityManager();
  
  private static HashMap<String,OptionParser> queryOptions=initQueryOptions();
  private static Log LOG = LogFactory.getLog(EjbQlAccessObject.class);
  
}
