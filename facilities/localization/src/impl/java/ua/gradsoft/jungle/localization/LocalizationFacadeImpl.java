package ua.gradsoft.jungle.localization;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import java.util.Map;
import java.util.TreeMap;
import javax.persistence.Entity;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import net.sf.ehcache.Cache;
import net.sf.ehcache.Element;
import net.sf.ehcache.store.MemoryStoreEvictionPolicy;
import ua.gradsoft.caching.CachingWrapper;
import ua.gradsoft.jungle.persistence.ejbqlao.EjbQlAccessObject;
import ua.gradsoft.jungle.persistence.jdbcex.RuntimeSqlException;
import ua.gradsoft.jungle.persistence.jpaex.JdbcConnectionWrapper;
import ua.gradsoft.jungle.persistence.jpaex.JpaEntityProperty;
import ua.gradsoft.jungle.persistence.jpaex.JpaHelper;

public class LocalizationFacadeImpl extends EjbQlAccessObject implements LocalizationFacade
{

    public boolean hasDefaultLanguageForCountry(String countryCode) {
       CountryLinfo ci = getCountryLinfo(countryCode);
       return ci.getDefaultLanguageCode()!=null;
    }

    public String getDefaultLanguageForCountry(String countryCode) {
        CountryLinfo ci = getCountryLinfo(countryCode);
        return ci.getDefaultLanguageCode();
    }

    /**
     * get list of ISO 639-1 languages, which is used inside country
     * @param countryCode ISO 3166 country code
     * @return
     */
    public List<String> getUsedLanguageIdsForCountry(String countryCode) {
        CountryLinfo ci = getCountryLinfo(countryCode);
        List<LanguageInfo> lli = ci.getUsedLanguages();
        List<String> retval = new ArrayList<String>();
        for(LanguageInfo li:lli) {
            retval.add(li.getCode());
        }
        return retval;
    }

    private CountryLinfo getCountryLinfo(String countryCode)
    {
        CountryLinfo ci;
        Cache metadataCache = getMetadataCache();
        Object o = metadataCache.get("CL"+countryCode);
        if (o!=null) {
            ci=(CountryLinfo)o;
        }else{
            ci = getEntityManager().find(CountryLinfo.class, countryCode);
            metadataCache.put(new Element("CL"+countryCode,ci));
        }
        return ci;
    }

    public List<String> getSupportedLanguagesForBundle(String bundleName) {
        BundleInfo bi = getBundleInfo(bundleName);
        List<String> retval = new ArrayList<String>();
        List<LanguageInfo> lli = bi.getSupportedLanguages();
        for(LanguageInfo li:lli) {
            retval.add(li.getCode());
        }
        return retval;
    }


    public String getDefaultLanguageForBundle(String bundleName) {
        BundleInfo bi = getBundleInfo(bundleName);
        return bi.getPrimaryLanguage().getCode();
    }

    private BundleInfo getBundleInfo(String bundleName)
    {
        Cache metadataCache = getMetadataCache();
        BundleInfo bi;
        Object o = metadataCache.get("BI"+bundleName);
        if (o!=null) {
                bi = (BundleInfo)o;
        } else {
                bi = getEntityManager().find(BundleInfo.class, bundleName);
                metadataCache.put(new Element("BI"+bundleName,bi));
        }
        return bi;
    }



    public List<List<String>>   translateFieldsByIds(String language,
                                                     String entityName,
                                                     List<Object> ids,
                                                     List<String> fieldNames)
    {
      TranslationTable tt = getTranslationTable(entityName);
      BundleInfo bi = tt.getBundle();
      Class entityClass;
      boolean translate = false;

      try {
        if (bi.getPrimaryLanguage().getCode().equals(language)){
         entityClass = Class.forName(entityName);       
        } else if (bi.getSupportedLanguages().contains(language)) {
         entityClass = Class.forName(entityName);
        } else {
          throw new IllegalArgumentException("Language "+language+" is not supported for bundle "+bi.getName());
        }
      }catch(ClassNotFoundException ex){
          throw new IllegalArgumentException("Cn't find class "+entityName,ex);
      }

      List<List<String>> retval = new ArrayList<List<String>>();
      if (!translate) {
          for(Object id: ids) {
            Object entity = getEntityManager().find(entityClass, id);
            List<String> row = new ArrayList<String>();
            for(String fieldName: fieldNames) {
              JpaEntityProperty<Object,String> p = JpaHelper.<Object,String>findJpaPropertyByName(entityClass, fieldName);
              row.add(p.getValue(entity));
            }
            retval.add(row);
          }
      }else{
          Cache dataCache = getDataCache();
          String translatedTable = tt.getTranslationTableName();
          StringBuilder sqlBuilder = new StringBuilder();
          sqlBuilder.append("select ");
          sqlBuilder.append(tt.getPkColumnName());
          for(String fieldName: fieldNames) {
              sqlBuilder.append(", ");
              String columnName = getColumnForTranslatedName(fieldName,language);
              if (columnName==null) {
                  throw new IllegalArgumentException("Field with name "+fieldName+" does not exists");
              }
              sqlBuilder.append(columnName);
          }
          sqlBuilder.append(" from ");
          sqlBuilder.append(translatedTable);
          sqlBuilder.append(" where ");
          sqlBuilder.append(tt.getPkColumnName());
          boolean doHack = true;

          JdbcConnectionWrapper wr = getJpaEx().getJdbcConnectionWrapper(getEntityManager(), true);
          Connection cn = wr.getConnection();
          try {
              if (cn.getMetaData().supportsMultipleResultSets() &&
                  cn.getMetaData().supportsBatchUpdates() &&
                  doHack
                  ) {
                      // incorrect but work with pgsql .
                      sqlBuilder.append("= ?");
                      String sql = sqlBuilder.toString();
                      PreparedStatement st = cn.prepareStatement(sql);
                      int i=0;
                      HashMap<Integer,List<String>> cachedRows = new HashMap<Integer,List<String>>();
                      for(Object id:ids) {
                          List<String> row = getCachedTranslation(id,entityClass,language, fieldNames);
                          if (row==null) {
                            st.setObject(1,id);
                            st.addBatch();
                          }else{
                            cachedRows.put(i, row);
                          }
                          ++i;
                      }
                      int[] results = st.executeBatch();
                      Iterator it = ids.iterator();
                      i=0;
                      do {
                        List<String> retvalRow = cachedRows.get(i);
                        if (retvalRow==null) {
                          Object id = it.next();
                          ResultSet rs = st.getResultSet();
                          retvalRow = new ArrayList<String>();
                          if (!rs.next()) {
                            //TODO: log ?
                            Object entity = getEntityManager().find(entityClass, id);
                            for(String fieldName: fieldNames) {
                              JpaEntityProperty<Object,String> p = JpaHelper.<Object,String>findJpaPropertyByName(entityClass, fieldName);
                              retvalRow.add(p.getValue(entity));
                            }
                          }else{
                            int nColumns = rs.getMetaData().getColumnCount();
                            for(int ii=0; ii<nColumns; ++ii) {
                              if (ii!=0) {  // ii!=0 to skip pk
                                retvalRow.add(rs.getString(ii+1));
                              }
                            }
                          }
                          putCachedTranslation(id,entityClass,language,fieldNames,retvalRow);
                        }
                        retval.add(retvalRow);
                        ++i;
                      }while(st.getMoreResults());
              } else {
                  sqlBuilder.append(" in (");
                  boolean isFirst=true;
                  int nAdded=0;
                  Map<Object,Integer> indexesByIds = new HashMap<Object,Integer>();
                  for(int i=0; i<ids.size(); ++i) {
                      if (!isFirst) {
                          sqlBuilder.append(", ");
                      }
                      Object id = ids.get(i);
                      List<String> row = getCachedTranslation(id,entityClass,language,fieldNames);
                      if (row==null) {
                        sqlBuilder.append("?");
                        isFirst=false;
                        ++nAdded;
                      }
                  }
                  sqlBuilder.append(")");
                  if (nAdded!=0) {
                    PreparedStatement st = cn.prepareStatement(sqlBuilder.toString());
                    for(int i=0; i<ids.size(); ++i) {
                      st.setObject(i+1, ids.get(i));
                      indexesByIds.put(ids.get(i),i);
                      retval.add(new ArrayList<String>());
                    }
                    ResultSet rs = st.executeQuery();
                    // note, that order in rs can be differ from order in it.
                    while(rs.next()) {
                      Object rsid = rs.getObject(1);
                      Integer rsind = indexesByIds.get(rsid);
                      if (rsind==null) {
                          // we receive id in result. which was not in out in clause.
                          // impossible.
                          throw new IllegalStateException("impossible: id not found, which was in sql 'in' clause");
                      }
                      List<String> retRow = retval.get(rsind);
                      for(int i=0; i<fieldNames.size(); ++i) {
                          String value = rs.getString(i+2);
                          retRow.add(value);
                      }
                      putCachedTranslation(rsid,entityClass,language,fieldNames,retRow);
                    }
                  }
                  // now fill cached or untranslated parts
                  int i=0;
                  for(List<String> row: retval) {
                     if (row.size()==0) {
                          List<String> crow = getCachedTranslation(ids.get(i),entityClass,language,fieldNames);
                          if (crow!=null) {
                              row.addAll(crow);
                          } else {
                              Object entity = getEntityManager().find(entityClass, ids.get(i));
                              for(String fieldName: fieldNames) {
                                JpaEntityProperty<Object,String> p = JpaHelper.<Object,String>findJpaPropertyByName(entityClass, fieldName);
                                row.add(p.getValue(entity));
                              }
                          }
                     }
                     ++i;
                  }

              }
          }catch(SQLException ex){
              throw new RuntimeSqlException(ex);
          }finally{
              wr.releaseConnection(cn);
          }

      }

      return retval;
    }

    public List<String>   translateFieldsById(String language,
                                           String entityName, Object id,
                                           List<String> fieldNames)
    {
      return translateFieldsByIds(language, entityName, Collections.singletonList(id),
                                  fieldNames).get(0);
    }

    public<T>  T  translateBean(T bean, String languageCode, boolean deep)
    {
      if (bean instanceof Collection) {
          return (T)translateBeans((Collection)bean,languageCode, deep);
      }else if (bean instanceof Map) {
          Map<Object,Object> m = (Map<Object,Object>)bean;
          for(Map.Entry<Object,Object> e: m.entrySet()) {
              Object tv = translateBean(e.getValue(),languageCode, deep);
              m.put(e.getKey(), tv);
          }
          return (T)m;
      }else if (bean.getClass().isAnnotationPresent(Entity.class)) {
          Collection<T> rb = translateBeans(Collections.<T>singletonList(bean),languageCode,deep);
          return rb.iterator().next();
      }else {
          // return unchanged
          return bean;
      }
    }


    /**
     * note, that beans must be detached.
     */
    public<T> Collection<T>  translateBeans(Collection<T> beans, String languageCode, boolean deep)
    {
        Iterator<T> it = beans.iterator();
        if (!it.hasNext()) {
            // collection is empty, return unchanged.
            return beans;
        }
        String lc = languageCode.toUpperCase();
        Class entityClass;
        List<JpaEntityProperty> allProperties;
        {
         T bean = it.next();
         entityClass = JpaHelper.findSameOrSuperJpaEntity(bean.getClass());
         if (entityClass==null) {
            // "atempt to translate not-entity class"
            return beans;
         }
         allProperties = JpaHelper.getAllJpaProperties(entityClass);
        }
        
        Map<String,TranslationTable> tts = new TreeMap<String,TranslationTable>();
        Map<String,BundleInfo> bis = new TreeMap<String, BundleInfo>();
        Map<String,JpaEntityProperty> propertiesToTranslate = new HashMap<String,JpaEntityProperty>();
        JpaEntityProperty idProperty=null;
        for(JpaEntityProperty p: allProperties) {          
            if (p.isId()) {
                idProperty=p;
                continue;
            }
            String key = p.getEntityClass().getName();
            if (!p.getPropertyClass().equals(String.class) &&
                !p.getPropertyClass().equals(Character.class)) {
                continue;
            }
            if (!tts.containsKey(key)) {
                TranslationTable tt = findTranslationTableForProperty(p);
                if (tt==null) {
                    continue;
                }
                propertiesToTranslate.put(p.getColumnName(),p);
                tts.put(key, tt);
                if (!bis.containsKey(key)) {
                    BundleInfo bi = tt.getBundle();
                    bis.put(key, bi);
                    boolean supportedLanguageFound = false;
                    for(LanguageInfo li: bi.getSupportedLanguages()) {
                        if (li.getName().equalsIgnoreCase(languageCode)) {
                            supportedLanguageFound=true;
                            break;
                        }
                    }
                    if (!supportedLanguageFound) {
                        throw new IllegalArgumentException("Language "+languageCode+" is not supported");
                    }
                }               
               
            }
        }

        // now prepare list of ids
        List<Object> ids = new ArrayList<Object>();
        for(T bean: beans) {
            ids.add(idProperty.getValue(bean));
        }

        Map<String,List<List<String>>> translatedPerClass = new TreeMap<String, List<List<String>>>();
        Map<String,List<JpaEntityProperty>> propertiesPerClass = new TreeMap<String,List<JpaEntityProperty>>();
        for(Map.Entry<String,TranslationTable> e: tts.entrySet()) {
          TranslationTable tt = e.getValue();
          List<String> fieldNames = new ArrayList<String>();
          List<JpaEntityProperty> fieldProperties = new ArrayList<JpaEntityProperty>();
          for(TranslationTableColumn tc: tt.getTranslatedColumns()) {
            String fieldName = normalizeFieldName(tc.getColumnName());
            fieldNames.add(fieldName);
            JpaEntityProperty fieldProperty  = propertiesToTranslate.get(tc.getColumnName());
            fieldProperties.add(fieldProperty);
          }

          List<List<String>> translatedForKey =  translateFieldsByIds(languageCode,
                                                                       e.getKey(), ids,
                                                                       fieldNames);

          translatedPerClass.put(e.getKey(), translatedForKey);
          propertiesPerClass.put(e.getKey(), fieldProperties);

        }


        it = beans.iterator();
        for(int i=0; it.hasNext() ;++i) {
          T bean = it.next();
          for(Map.Entry<String,List<List<String>>> e: translatedPerClass.entrySet()) {
            List<String> translatedRow = e.getValue().get(i);
            List<JpaEntityProperty> fieldProperties =  propertiesPerClass.get(e.getKey());

            for(int j=0; j<fieldProperties.size(); ++j) {
                fieldProperties.get(j).setValue(bean,translatedRow.get(j));
            }
          }

          // now translate non-trivial complex properties
          if (deep) {
            for(JpaEntityProperty p: allProperties) {
                Class propertyClass = p.getPropertyClass();
                if (!propertyClass.isPrimitive()) {
                  Object v = p.getValue(bean);
                  Object tv = translateBean(v,languageCode,deep);
                  p.setValue(bean, tv);
                }
            }
          }
        }

        return beans;

    }


    
    private String getColumnForTranslatedName(String fieldName, String language)
    {
        return normalizeFieldName(fieldName)+"_"+language;
    }

    private String normalizeFieldName(final String fieldName)
    {
      String name = fieldName;
      if (name.endsWith("_eng")||name.endsWith("_ENG")) {
          name=name.substring(0,name.length()-4);
      }else if (fieldName.endsWith("_en")||fieldName.endsWith("_EN")) {
          name=name.substring(0, name.length()-3);
      }
      return name.toUpperCase();
    }

    private TranslationTable getTranslationTable(String entityClassName)
    {
      List<TranslationTable> ltt;
      Cache metadataCache = getMetadataCache();
      Object o = metadataCache.get("TT"+entityClassName);
      if (o!=null) {
          ltt = (List<TranslationTable>)o;
      } else {
          ltt = executeQuery(TranslationTable.class,
                    "select tt from TranslationTable tt \n"+
                                 " where tt.entityClassName=:name",
                             Collections.<String,Object>singletonMap("name", entityClassName),
                             Collections.<String,Object>emptyMap());
          metadataCache.put(new Element("TT"+entityClassName,ltt));
      }
      if (ltt.size()==0) {
          throw new IllegalArgumentException("can't find bungle for entity class name "+entityClassName);
      }
      TranslationTable tt = ltt.get(0);        
      return tt;
    }

    private TranslationTable  findTranslationTableForProperty(JpaEntityProperty property)
    {
      List<TranslationTable> ltt;  
      String entityClassName = property.getEntityClass().getName();
      Cache metadataCache = getMetadataCache();
      Object o = metadataCache.get("TT"+entityClassName);
      if (o!=null) {
          ltt = (List<TranslationTable>)o;
      } else {
          ltt = executeQuery(TranslationTable.class,
                    "select tt from TranslationTable tt \n"+
                                 " where tt.entityClassName=:name",
                             Collections.<String,Object>singletonMap("name", entityClassName),
                             Collections.<String,Object>emptyMap());
          metadataCache.put(new Element("TT"+entityClassName,ltt));
      }      
      if (ltt.size()==0) {
          return null;          
      }
      TranslationTable tt = ltt.get(0);
      for(TranslationTableColumn c: tt.getTranslatedColumns()) {
          if (c.getColumnName().equalsIgnoreCase(property.getColumnName())) {
              return tt;
          }
      }
      return null;
    }

    private List<String> getCachedTranslation(Object id, Class entity, String language, List<String> fieldNames)
    {
        Cache dataCache = getDataCache();      
        Element e = dataCache.get(entity.getName()+","+id.toString());
        if (e==null) {
            return null;
        }
        Map<String,Map<String,String>> hss = (Map<String,Map<String,String>>)e.getValue();
        if (hss==null) {
            return null;
        }
        Map<String,String> hs = hss.get(language);
        if (hs==null) {
            return null;
        }
        List<String> retval = new ArrayList<String>();
        for(String fieldName: fieldNames) {
            String value = hs.get(fieldName);
            if (value==null) {
                return null;
            }
            retval.add(value);
        }
        return retval;
    }

    public void putCachedTranslation(Object id, Class entityClass, String language, List<String> fieldNames, List<String> values)
    {
       Cache dataCache = getDataCache();
       String key = entityClass.getName()+","+id.toString();
       Element e = dataCache.get(key);
       HashMap<String,HashMap<String,String>> hss;
       if (e==null) {
           hss = new HashMap<String,HashMap<String,String>>();
       } else {
           hss = (HashMap<String,HashMap<String,String>>)e.getValue();
       }
       HashMap<String,String> hs = hss.get(language);
       if (hs==null) {
           hs = new HashMap<String,String>();
           hss.put(language, hs);
       }
       for(int i=0; i<fieldNames.size(); ++i) {
           hs.put(fieldNames.get(i), values.get(i));
       }
       dataCache.put(new Element(key,hss));
    }
    
    private Cache  getMetadataCache()
    { return getCache(LOCALIZATION_METADATA_CACHE); }

    private Cache  getDataCache()
    { return getCache(LOCALIZATION_DATA_CACHE); }


    private Cache  getCache(String name)
    {
        CachingWrapper wr = CachingWrapper.getInstance();
        Cache retval = wr.getCacheManager().getCache(name);
        if (retval==null) {
             retval = new Cache(name,
                                1000, // ,axElementsInMemoryForCache(),
                                MemoryStoreEvictionPolicy.LRU, // MemoryStoreEvictionPolicy(),
                                false, // cacheConfig.getOverflowToDisk(),
                                null,
                                false, // rteral,
                                (60*60)*3, //getTimeToLive(),
                                (60*60)*2,  //getTimeToIdle(),
                                false, // cacheConfig.getDiskPersistent(),
                                60*3, // cacheConfig.getDiskExpireThreadInterval(),
                                 null
                                 );
             wr.getCacheManager().addCache(retval);
        }
        return retval;
    }


    public EntityManager getEntityManager()
    {
      return en_;  
    }

    private static String LOCALIZATION_METADATA_CACHE = "_LOCALIZATION_MD";
    private static String LOCALIZATION_DATA_CACHE = "_LOCALIZATION_DATA";

    @PersistenceContext
    EntityManager en_;
}
