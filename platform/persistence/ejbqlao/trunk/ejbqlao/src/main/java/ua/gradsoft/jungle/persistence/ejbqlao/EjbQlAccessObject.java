package ua.gradsoft.jungle.persistence.ejbqlao;


import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import javax.persistence.Entity;
import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.persistence.TemporalType;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import ua.gradsoft.jungle.persistence.cluster_keys.ClusterKeys;
import ua.gradsoft.jungle.persistence.cluster_keys.SequenceKey;
import ua.gradsoft.jungle.persistence.ejbqlao.util.Pair;
import ua.gradsoft.jungle.persistence.jpaex.JdbcConnectionWrapper;
import ua.gradsoft.jungle.persistence.jpaex.JpaEx;

/**
 * Object, which erncapsulate remote entity manager.
 *(Generic implementation of CRUDFacade)
 **/
public abstract class EjbQlAccessObject implements CRUDFacade
{       

    @Override
    public <T>       void  create(T newEntity)
    {
        getEntityManager().persist(newEntity);
    }

    @Override
    public <T>       void  update(T newEntity)
    {
        getEntityManager().merge(newEntity);
    }

    @Override
    public void  remove(Object id)
    {
        getEntityManager().remove(id);
    }

    @Override
    public <T>       T     find(Class<T> tClass, Object id)
    {
        return getEntityManager().find(tClass, id);
    }
    

  @Override
  public <T> List<T> executeQuery(Class<T> tClass, String ejbql)
  {
   return executeQuery(tClass,ejbql,Collections.<Object>emptyList(),Collections.<String,Object>emptyMap());   
  }

  //@Override
  public <T> List<T> executeQuery(Class<T> tClass, String ejbql, List<Object> positionParameters)
  {
    return executeQuery(tClass, ejbql,positionParameters,Collections.<String,Object>emptyMap());
  }

  @Override
  public <T> List<T> executeQuery(Class<T> tClass, String ejbql, List<Object> positionParameters, Map<String,Object> options)
  {
   boolean isJdbcNative=useJdbcMapping(tClass, options);
   List<T> retval;
   if (isJdbcNative){
     JdbcConnectionWrapper cnw = getJpaEx().getJdbcConnectionWrapper(getEntityManager(), true);
     Connection cn = cnw.getConnection();
     try {
         PreparedStatement st = cn.prepareStatement(ejbql);
         applyPositionalParameters(st,positionParameters);
         applyOptions(st,options);
         ResultSet rs = st.executeQuery();
         Class<?> resultClass = getClassForJdbcQueryResultRow(tClass);
         retval = transformResultSetToList(rs, tClass, resultClass);
     }catch(SQLException ex){
         throw new DatabaseAccessException("Exception during execute",ex);
     }finally{
         cnw.releaseConnection(cn);
     }
   }else{
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
     retval = q.getResultList();
   }
   return retval;
  }

  @Override
  public <T> List<T> executeQuery(Class<T> tClass, String ejbql, Map<String,Object> namedParameters, Map<String,Object> options)
  {
     boolean isJdbcNative = useJdbcMapping(tClass, options);
     List<T> retval = null;
     if (isJdbcNative) {
        JdbcConnectionWrapper cnw = getJpaEx().getJdbcConnectionWrapper(getEntityManager(), true);
        Connection cn = cnw.getConnection();
        try {
          SqlPositionalQueryWithParams params = NamedToPositionalParamsSqlTransformer.translate(ejbql, namedParameters);
          PreparedStatement st = cn.prepareStatement(params.getQuery());
          applyPositionalParameters(st,params.getParameters());
          applyOptions(st,options);
          ResultSet rs = st.executeQuery();
          Class<?> resultClass = getClassForJdbcQueryResultRow(tClass);
          retval = transformResultSetToList(rs, tClass, resultClass);
        }catch(SQLException ex){
            throw new DatabaseAccessException("Exception during execute",ex);
        }finally{
            cnw.releaseConnection(cn);
        }
     }else{
        // filters must be enabled before creation of query.
        enableFilters(namedParameters, options);
        Query q = createQuery(ejbql, options);
        applyNamedParameters(q,namedParameters);
        applyOptions(q,options);
        retval = q.getResultList();
     }
     return retval;     
  }

  @Override
  public int executeUpdate(String ejbql, Map<String,Object> namedParameters, Map<String,Object> options)
  {
    boolean isJdbcNative = useJdbcMapping(null, options);
    if (isJdbcNative) {
        JdbcConnectionWrapper cnw = getJpaEx().getJdbcConnectionWrapper(getEntityManager(), false);
        Connection cn = cnw.getConnection();
        try {
           SqlPositionalQueryWithParams params = NamedToPositionalParamsSqlTransformer.translate(ejbql, namedParameters);
           PreparedStatement st = cn.prepareStatement(params.getQuery());
           applyPositionalParameters(st,params.getParameters());
           applyOptions(st,options);
           return st.executeUpdate();
        }catch(SQLException ex){
           throw new DatabaseAccessException("Exception during execute",ex);
        }finally{
            cnw.releaseConnection(cn);
        }
    }else{
      Query q = createQuery(ejbql, options);
      applyNamedParameters(q, namedParameters);
      applyOptions(q,options);
      return q.executeUpdate();
    }
  }

  @Override
  public <T,C> List<T>  queryByCriteria(Class<T> tClass, C criteria)
  {
    return queryByCriteria(tClass, criteria, Collections.<String,Object>emptyMap());
  }

  @Override
  public <T,C> List<T>  queryByCriteria(Class<T> tClass, C criteria, Map<String,Object> options)
  {
      CriteriaHelper criteriaHelper = createHelperObjectWithClassSuffix(criteria, "CriteriaHelper", CriteriaHelper.class);
      QueryWithParams qp = criteriaHelper.getSelectQueryWithParams(criteria);
      String query = qp.getQuery();
      Map<String,Object> namedParameters = qp.getNamedParameters();
      Map<String,Object> noptions = qp.getOptions();
      noptions.putAll(options);
      return executeQuery(tClass, query, namedParameters, noptions);
  }

  @Override
  public <T extends Number,C>  T queryCountByCriteria(Class<T> tClass, C criteria)
  {
      CriteriaHelper criteriaHelper = createHelperObjectWithClassSuffix(criteria, "CriteriaHelper", CriteriaHelper.class);
      QueryWithParams qp = criteriaHelper.getCountQueryWithParams(criteria);
      String query = qp.getQuery();
      Map<String,Object> namedParameters = qp.getNamedParameters();
      Map<String,Object> noptions = qp.getOptions();
      List ll = executeQuery(Long.class, query, namedParameters, noptions);
      if (ll.size()!=1) {
          throw new DatabaseAccessException("count query not return one row:"+query);
      }
      Object o = ll.get(0);
      Number retval;
      if (o instanceof Number) {
          retval = ((Number)o);
          if (tClass.isAssignableFrom(Long.class)||
              tClass.isAssignableFrom(long.class)
                  ) {
              return (T)(new Long(retval.longValue()));
          }else if (tClass.isAssignableFrom(Integer.class)
                  ||tClass.isAssignableFrom(int.class)){
              return (T)(new Integer(retval.intValue()));
          }else if (tClass.isAssignableFrom(Short.class)
                  ||tClass.isAssignableFrom(short.class)
                  ){
              return (T)(new Short(retval.shortValue()));
          }else if (tClass.isAssignableFrom(BigDecimal.class)){
              if (retval instanceof BigDecimal) {
                  return (T)((BigDecimal)retval);
              }else if (retval instanceof BigInteger){
                  BigInteger bi = (BigInteger)retval;
                  return (T)(new BigDecimal(bi));
              }else{
                  return (T)BigDecimal.valueOf(retval.longValue());
              }
          }else if (tClass.isAssignableFrom(BigInteger.class)){
              if (retval instanceof BigInteger) {
                  return (T)retval;
              }else if (retval instanceof BigDecimal){
                  BigDecimal bd = (BigDecimal)retval;
                  return (T)bd.toBigInteger();
              }else {
                  return (T)BigInteger.valueOf(retval.longValue());
              }
          }else if (tClass.isAssignableFrom(Double.class)){
              return (T)(new Double(retval.doubleValue()));
          }else if (tClass.isAssignableFrom(Float.class)){
              return (T)(new Float(retval.floatValue()));
          }else {
              throw new IllegalArgumentException("class of return value must be number ");
          }
      }else{
          throw new DatabaseAccessException("count query return non-number:"+query);
      }   
  }

  @Override
  public <C> int updateWithCommand(C command)
  {
     CommandHelper helper = createHelperObjectWithClassSuffix(command, "CommandHelper", CommandHelper.class);
     QueryWithParams params = helper.getQueryWithParams(command);
     return executeUpdate(params.getQuery(), params.getNamedParameters(), params.getOptions());     
  }


  /**
   * Generate next id for class entityClass, if some field in entityClass POJO
   * definition was marked as SequenceKey
   * @param <T> - type of key
   * @param <E> - type of POJP entity.
   * @param entityClass
   * @param idClass
   * @return next sequence or null, if such sequence does not exists.
   */
  public<T,E> T generateNextSequenceKey(Class<E> entityClass, Class<T> idClass)
  {
      Annotation entityAnnotation = entityClass.getAnnotation(Entity.class);
      if (entityAnnotation==null) {
          throw new IllegalArgumentException("Class" +entityClass.getName()+" has not @Entity annotation");
      }
      SequenceKey key = getSequenceKeyAnnotation(entityClass,null,null);
      if (key==null) {
          throw new IllegalArgumentException("SequenceKey annotation is missing in class "+entityClass.getName());
      }
      Object retval=null;
      switch(key.type()) {
          case CLUSTER:
          {
              if (idClass.isAssignableFrom(BigDecimal.class)) {
                  retval = ClusterKeys.generateBigDecimalClusterKeyBySequence(
                                    key.sequenceName(), getEntityManager(), getJpaEx()
                                  );
              } else if (idClass.isAssignableFrom(String.class)) {
                  retval = ClusterKeys.generateStringClusterKeyBySequence(
                                    key.sequenceName(), getEntityManager(), getJpaEx()
                                    );
              } else {
                  throw new IllegalArgumentException("type of cluster key must be BigDecimal or String");
              }
          }
          break;
          case ORDINARY:
          {
              long lretval = getJpaEx().getNextSequenceNumber(key.sequenceName(), getEntityManager());
              if (idClass.isAssignableFrom(Long.class)) {
                  retval = new Long(lretval);
              } else if (idClass.isAssignableFrom(BigDecimal.class)) {
                  retval = BigDecimal.valueOf(lretval);
              } else if (idClass.isAssignableFrom(String.class)) {
                  retval = Long.toHexString(lretval);
              } else {
                  throw new IllegalArgumentException("type of ordinary key must be Long or BigDecimal or String");
              }
          }
          break;
          default:
              throw new IllegalArgumentException("Unknow type of key:"+key.type());
      }
      return (T)retval;
  }    
      
      
  private SequenceKey getSequenceKeyAnnotation(Class<?> entityClass, Method[] methodHolder, Field[] fieldHolder)
  {
      SequenceKey keyAnnotation=null;
      // now search for Id
      Method[] methods = entityClass.getDeclaredMethods();
      for(Method m:methods) {
          keyAnnotation = m.getAnnotation(SequenceKey.class);
          if (keyAnnotation!=null) {
              if (methodHolder!=null) {
                  methodHolder[0]=m;                  
              }
              break;
          }
      }
      if (keyAnnotation==null) {
          // now search in fields
          Field[] fields = entityClass.getDeclaredFields();
          for(Field field: fields) {
              keyAnnotation = field.getAnnotation(SequenceKey.class);
              if (keyAnnotation!=null) {
                  if (fieldHolder!=null) {
                      fieldHolder[0]=field;
                  }
                  break;
              }
          }
      }
      if (keyAnnotation==null) {
          // may be super is entity - then let's search in super.
          Class superClass = entityClass.getSuperclass();
          if (superClass!=null) {
              Annotation entityAnnotation = superClass.getAnnotation(Entity.class);
              if (entityAnnotation!=null) {
                  return getSequenceKeyAnnotation(superClass,methodHolder,fieldHolder);
              }
          }
      }
      return keyAnnotation;

  }

  private Query createQuery(String ejbql, Map<String, Object> options)
  {
      boolean isNative=false;
      Class resultClass = null;
      String resultSetMapping = null;
      if (options!=null) {
          Object o = options.get("native");
          if (o!=null) {
            isNative=ObjectParser.parseBoolean(o);
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


  protected void applyNamedParameters(Query query, Map<String, ?> params)
  {
    for(Map.Entry<String,?> param: params.entrySet()) {
        applyNamedParameter(query,param);
    } 
  }
  
  private void applyNamedParameter( Query query, Map.Entry<String,?> param)
  {
         String name = param.getKey();
         if (getJpaEx().isFiltersSupported()) {
             int dotIndex = name.indexOf('.');
             if (dotIndex!=-1) {
                 // do nothing - parameter was set in enableFilters
                 return;
             }
         }
         Object value = param.getValue();
         if (value instanceof Date) {
           query.setParameter(name, (Date)value, TemporalType.TIMESTAMP);
         }else if (value instanceof List || value.getClass().isArray()) {
           List<Object> list;
           if (value instanceof List) {
             list = (List<Object>)value;
           }else{
             Object[] array = (Object[])value;  
             list = Arrays.asList(array);  
           }
           if (ObjectParser.isHibernateType(list)) {
               // call setParameter via reflection
               //next code is equivalent to:
               //
               //org.hibernate.ejb.QueryImpl hs = (org.hibernate.ejb.QueryImpl)query;
               //org.hibernate.Query hq = hs.getHibernateQuery();
               //Pair<org.hibernate.Type,Object> pair = ObjectParser.getHibernateTypeValue(list);
               //hq.setParameter(name,pair.frs,pair.snd);
               //

               Pair<Object,Object> pair = ObjectParser.getHibernateTypedParameter(list);
               Method getHibernateQueryMethod=lazyInitHibernateEjbGetHibernateQueryMethod(query);
               Object hq=null;
               try {
                 hq=getHibernateQueryMethod.invoke(query, new Object[0]);
               }catch(IllegalAccessException ex){
                 throw new DatabaseAccessException("Imposible: org.hibernate.ejb.HibernateQuery.getHibernateQuery is not accessible",ex);
               }catch(InvocationTargetException ex){
                   throw new DatabaseAccessException("Error during set of query params",ex);
               }
               Method setParamWithType = lazyInitHibernateSetParamWithTypeMethod(query);
               Object[] params = new Object[] {
                  name, pair.getSnd(), pair.getFrs()
               };
               try {
                 setParamWithType.invoke(hq, params);
               }catch(IllegalAccessException ex){
                   throw new DatabaseAccessException("Imposible: org.hibernate.Query.setParam is not accessible",ex);
               }catch(InvocationTargetException ex){
                   throw new DatabaseAccessException("Error during set of query params",ex);
               }
           } else {
              query.setParameter(name, ObjectParser.parseListAsQueryParameter(list));
           }
       }else{
           query.setParameter(name, value);
       }               
  }

  protected void applyPositionalParameters(PreparedStatement st, List<Object> params) throws SQLException
  {
    int i=0;
    for(Object param: params)
    {

      if (param instanceof List) {
          List<Object> list = (List<Object>)param;
          st.setObject(i+1, ObjectParser.parseListAsQueryParameter(list));
      }else if (param.getClass().isArray()){
          Object[] arr = (Object[])param;
          List<Object> list = Arrays.asList(arr);
          st.setObject(i+1, ObjectParser.parseListAsQueryParameter(list));
      }else{
          st.setObject(i+1, param);
      }
      ++i;
    }
  }
  
  protected void applyOptions(Query query, Map<String,?> options)
  {
    for(Map.Entry<String,?> option: options.entrySet())  {
        applyOption(query,option, options);
    }
  }
  
  private void applyOption(Query query, Map.Entry<String,?> option, Map<String,?> options)
  {
    String key = option.getKey();
    OptionParser evaluator = queryOptions.get(key);
    if (evaluator==null) {
        throw new IllegalArgumentException("Invalid option:"+key);
    }
    evaluator.apply(this, query, option.getValue(), options);
  }

  private void applyOptions(Statement st, Map<String,?> options)
  {
      for(Map.Entry<String,?> option: options.entrySet())
      {
        applyOption(st,option,options);  
      }
  }

  private void applyOption(Statement st, Map.Entry<String,?> option, Map<String,?> options)
  {
      OptionParser evaluator = queryOptions.get(option.getKey());
      if (evaluator==null) {
          throw new IllegalArgumentException("Invalid option:"+option.getKey());
      }
      evaluator.apply(this, st, option.getValue(), options);
  }


  private boolean useJdbcMapping(Class resultClass, Map<String,Object> options)
  {
    boolean retval = false;
    Object o = options.get("jdbcnative");
    if (o!=null) {
        retval = ObjectParser.parseBoolean(o);
    }else{
        if (resultClass!=null) {
          o=options.get("native");
          boolean n1=false;
          if (o!=null) {
            n1 = ObjectParser.parseBoolean(o);
          }
          if (n1 && java.util.Map.class.isAssignableFrom(resultClass)) {
            retval=true;
          }
        }
    }
    return retval;
  }


  protected Class<?> getClassForJdbcQueryResultRow(Class<?> tClass)
  {
    Class<?> resultClass;
    if (java.util.Map.class.isAssignableFrom(tClass)) {
      if (tClass.isInterface()) {
         resultClass=java.util.HashMap.class;
      }else{
         resultClass=tClass;
      }
    }else if(java.util.List.class.isAssignableFrom(tClass)){
      if (tClass.isInterface()) {
         resultClass=java.util.ArrayList.class;
      }else{
         resultClass=tClass;
      }
    }else if(tClass.isAssignableFrom(java.lang.Object.class)){
      resultClass=java.util.ArrayList.class;
    }else{
      throw new IllegalArgumentException("unknown result class for jdbc native query");
    }
    return resultClass;
  }

  private<T> List<T>  transformResultSetToList(ResultSet rs, Class<T> tClass, Class<?> rClass) throws SQLException
  {
    List<T> retval = new LinkedList<T>();
    if (java.util.Map.class.isAssignableFrom(tClass)) {
      try {
        while(rs.next()) {
          Map<String,Object> row = (Map<String,Object>)rClass.newInstance();
          for(int i=1; i<=rs.getMetaData().getColumnCount(); ++i){
            String columnLabel = rs.getMetaData().getColumnLabel(i);
            Object o = rs.getObject(i);
            row.put(columnLabel, o);
          }
          retval.add((T)row);
        }
      }catch(InstantiationException ex){
         throw new IllegalArgumentException("Can't create oject of type "+rClass.getName(),ex);
      }catch(IllegalAccessException ex){
         throw new IllegalArgumentException("Can't create oject of type "+rClass.getName(),ex);
      }
    }else if((tClass!=null && List.class.isAssignableFrom(tClass)) || List.class.isAssignableFrom(rClass)){
      try {
        while(rs.next()) {
            List<Object> row = (List<Object>)rClass.newInstance();
            for(int i=1; i<=rs.getMetaData().getColumnCount();++i){
                row.add(rs.getObject(i));
            }
            retval.add((T)row);
        }
      }catch(InstantiationException ex){
         throw new IllegalArgumentException("Can't create oject of type "+rClass.getName(),ex);
      }catch(IllegalAccessException ex){
         throw new IllegalArgumentException("Can't create oject of type "+rClass.getName(),ex);
      }
    }else{
        //may be in future create pluggable custom transformers.
        throw new IllegalArgumentException("Can't cast resultset to "+tClass.getName());
    }
    return retval;
  }

  private void enableFilters(Map<String,Object> namedParameters, Map<String,Object> options) {

      for(Map.Entry<String,Object> e : namedParameters.entrySet() ) {
         String name = e.getKey();
         int idx = name.indexOf('.');
         if (idx!=-1) {
             String filterName = e.getKey().substring(0, idx);
             String paramName = name.substring(idx+1);
             Object value = e.getValue();
             if (value instanceof List) {
                 List<Object> list = (List<Object>)value;
                 value = ObjectParser.parseListAsQueryParameter(list);
             }
             getJpaEx().setFilterParameter(getEntityManager(),
                                               filterName, paramName, value);
             //we doesw not remove, becouse afraid that map implementation
             // can be read-only (instead skip params with '.' in applyNamedParameters)
             //namedParameters.remove(name);
         }
     } 
     Object oFilterName = options.get("enable-filter");
     if (oFilterName!=null) {
         enableFilter(oFilterName.toString());
     }
     oFilterName = options.get("disable-filter");
     if (oFilterName!=null) {
         disableFilter(oFilterName.toString());
     }
  }


  private void enableFilter(String filterName)
  {
    if (getJpaEx().isFiltersSupported()) {
        getJpaEx().setFilterEnabled(getEntityManager(), filterName, true);
    }else{
        LOG.warn("JPA subsystem does not support filters, ignorign enableFilter");
    }
  }

  private void disableFilter(String filterName)
  {
    if (getJpaEx().isFiltersSupported()) {
        getJpaEx().setFilterEnabled(getEntityManager(), filterName, false);
    }else{
        LOG.warn("JPA subsystem does not support filters, ignorign disableFilter");
    }
  }


  public static interface OptionParser
  {
      void apply(EjbQlAccessObject ao, Query q, Object o, Map<String,?> opts);

      void apply(EjbQlAccessObject ao, Statement st, Object o, Map<String,?> opts);

      public static class NothingOptionParser implements OptionParser
      {
        public void apply(EjbQlAccessObject ao, Query q, Object o, Map<String,?> opts) {}
        public void apply(EjbQlAccessObject ao, Statement st, Object o, Map<String,?> opts) {}
      }
      
      public static final NothingOptionParser NOTHING = new NothingOptionParser();
  }




  private static HashMap<String,OptionParser> initQueryOptions()
  {
      HashMap<String,OptionParser> a = new HashMap<String,OptionParser>();
      a.put("maxResults", new OptionParser()
      {
         public void apply(EjbQlAccessObject ao, Query q,Object o,Map<String,?> opts)  {
             q.setMaxResults(ObjectParser.parseInt(o));             
         }
         public void apply(EjbQlAccessObject ao, Statement st, Object o, Map<String,?> opts){
             try {
               st.setMaxRows(ObjectParser.parseInt(o));
             }catch(SQLException ex){
                 throw new DatabaseAccessException("Exception during setting query option",ex);
             }
         }
      }
              );
      a.put("firstResult", new OptionParser()
      {
         public void apply(EjbQlAccessObject ao, Query q,Object o, Map<String,?> opts)  {
             q.setFirstResult(ObjectParser.parseInt(o));                    
         }
         public void apply(EjbQlAccessObject ao, Statement st, Object o, Map<String,?> opts)
         {
            int io = ObjectParser.parseInt(o);
            if (io!=0) {
                throw new IllegalArgumentException("firstResult options is not supported in jdbc-sql queries");
            }
         }
      }              
              );
      a.put("native", OptionParser.NOTHING);
      a.put("jdbcnative", OptionParser.NOTHING);
      a.put("resultClass", OptionParser.NOTHING);
      a.put("resultSetMapping", OptionParser.NOTHING);
      a.put("nocache", OptionParser.NOTHING);
      a.put("enable-filter", new OptionParser(){

            public void apply(EjbQlAccessObject ao, Query q, Object o, Map<String, ?> opts) {
                ao.enableFilter(o.toString());
            }

            public void apply(EjbQlAccessObject ao, Statement st, Object o, Map<String, ?> opts) {
                ao.enableFilter(ao.toString());
            }

      });

      a.put("enable-filters", new OptionParser(){

            public void apply(EjbQlAccessObject ao, Query q, Object o, Map<String, ?> opts) {
                enableFilters(ao ,o);
            }

            public void apply(EjbQlAccessObject ao, Statement st, Object o, Map<String, ?> opts) {
                enableFilters(ao ,o);
            }

            private void enableFilters(EjbQlAccessObject ao, Object o){
                if (ao.getJpaEx().isFiltersSupported()) {
                    if (o instanceof List) {
                        List l = (List)o;
                        for(Object oo: l) {
                           ao.enableFilter(oo.toString());
                        }
                    }else{
                        LOG.warn("attempt to set filter with non-string type:"+o.toString());
                    }
                }
            }

      });


      a.put("disable-filter", new OptionParser(){

            public void apply(EjbQlAccessObject ao, Query q, Object o, Map<String, ?> opts) {
                ao.disableFilter(o.toString());
            }

            public void apply(EjbQlAccessObject ao, Statement st, Object o, Map<String, ?> opts) {
                ao.disableFilter(o.toString());
            }


      });

      a.put("disable-filters", new OptionParser(){

            public void apply(EjbQlAccessObject ao, Query q, Object o, Map<String, ?> opts) {
                disableFilters(ao, (List<String>)o);
            }

            public void apply(EjbQlAccessObject ao, Statement st, Object o, Map<String, ?> opts) {
                disableFilters(ao, (List<String>)o);
            }

            private void disableFilters(EjbQlAccessObject ao, List<String> l)
            {
               for(String s: l) {
                   ao.disableFilter(s);
               }
            }


      });


      return a;  
  }

  private <C, CH> CH createHelperObjectWithClassSuffix(C x, String suffix, Class<CH> helperInterface )
  {
      Class helperClass = findHelperClassWithSuffix(x.getClass(),suffix);
      if (helperClass == null) {
          throw new IllegalArgumentException("Invalid criteria or command: can't find  helper for class "+x.getClass().getName()+" with suffix "+suffix);
      }
      Object o = null;
      try {
        o = helperClass.newInstance();
      } catch (InstantiationException ex) {
          throw new IllegalArgumentException("Can't instantiate class "+helperClass.getName(),ex);
      } catch (IllegalAccessException ex){
          throw new IllegalArgumentException("Can't instantiate class "+helperClass.getName(),ex);
      }
      if (!(helperInterface.isAssignableFrom(helperClass))) {
          throw new IllegalArgumentException("bad helper: "+helperClass.getName()+" must implement "+helperInterface.getName());
      }
      return (CH)o;
  }


  private Class findHelperClassWithSuffix(Class criteriaClass, String suffix)
  {
    if (criteriaClass==null ||
        criteriaClass.equals(Object.class) ||
        criteriaClass.equals(Enum.class)) {
        return null;
    }
    Class retval=null;
    String criteriaHelperClassName = criteriaClass.getName()+suffix;
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
    retval = findHelperClassWithSuffix(criteriaClass.getSuperclass(),suffix);
    if (retval!=null) {
        return retval;
    }
    Class[] interfaces = criteriaClass.getInterfaces();
    for(int i=0; i<interfaces.length; ++i) {
        retval = findHelperClassWithSuffix(interfaces[i],suffix);
        if (retval!=null) {
            return retval;
        }
    }
    return retval;
  }

  private Class lazyInitHibernateQueryClass(Query query)
  {
    if (hibernateEjbQueryClass==null) {
       try {
         hibernateEjbQueryClass = Class.forName("org.hibernate.ejb.HibernateQuery", true, query.getClass().getClassLoader());
       } catch (ClassNotFoundException ex) {
         throw new DatabaseAccessException("Can't find org.hibernate.ejb.HibernateQuery class during processing of hibernate-specific extension",ex);
       }
       if (!hibernateEjbQueryClass.isAssignableFrom(query.getClass())) {
         throw new DatabaseAccessException("hibernate-specific extension passed to non-hibernate query:"+query.getClass());
       }
       try {
         hibernateQueryClass = Class.forName("org.hibernate.Query", true, query.getClass().getClassLoader());
       } catch (ClassNotFoundException ex) {
          throw new DatabaseAccessException("Can't find org.hibernate.ejb.HibernateQuery class during processing of hibernate-specific extension",ex);
       }
    }   
    return hibernateEjbQueryClass;
  }
  
  private Method lazyInitHibernateSetParamWithTypeMethod(Query query)
  {
   if (hibernateQuerySetParameterMethod==null) {   
     lazyInitHibernateQueryClass(query);
     Class[] argClasses = new Class[3];
     argClasses[0]=String.class;
     argClasses[1]=Object.class;
     try {
       argClasses[2]=Class.forName("org.hibernate.type.Type",false,query.getClass().getClassLoader());
     }catch(ClassNotFoundException ex){
         throw new DatabaseAccessException("can't find org.hibernate.type.Type",ex);
     }
     try {
       hibernateQuerySetParameterMethod=
                      hibernateQueryClass.getMethod("setParameter", argClasses);
     }catch(NoSuchMethodException ex){
         throw new DatabaseAccessException("can't find hibernate setParameter method",ex);
     }
   }
   return hibernateQuerySetParameterMethod;
  }

  private Method lazyInitHibernateEjbGetHibernateQueryMethod(Query query)
  {
   if (hibernateEjbQueryGetHibernateQueryMethod==null) {
     lazyInitHibernateQueryClass(query);
     Class[] argClasses = new Class[0];
     try {
       hibernateEjbQueryGetHibernateQueryMethod=
                      hibernateEjbQueryClass.getMethod("getHibernateQuery", argClasses);
     }catch(NoSuchMethodException ex){
         throw new DatabaseAccessException("can't find hibernate getHibernateQuery method",ex);
     }
   }
   return hibernateEjbQueryGetHibernateQueryMethod;
  }


  /**   
   * accessible only from local.
   * Must be overloaded from subclasses.
   * @return entityManager
   */
  public abstract EntityManager getEntityManager();


  /**
   * accessible only from local. By default return JpaEx singleton which
   * must be configured before first call to getJpaEx;
   * Can be overloaded from subclasses.
   * @return JpaEx
   */  
  public JpaEx  getJpaEx()
  {
    return JpaEx.getInstance();
  }

  private static Class hibernateQueryClass=null;
  private static Class hibernateEjbQueryClass=null;
  private static Method  hibernateQuerySetParameterMethod=null;
  private static Method  hibernateEjbQueryGetHibernateQueryMethod=null;

  private static HashMap<String,OptionParser> queryOptions=initQueryOptions();
  private static Log LOG = LogFactory.getLog(EjbQlAccessObject.class);
  
}
