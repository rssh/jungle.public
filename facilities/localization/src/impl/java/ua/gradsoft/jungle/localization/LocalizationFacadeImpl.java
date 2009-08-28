package ua.gradsoft.jungle.localization;

import java.io.Serializable;
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
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import ua.gradsoft.caching.CachingWrapper;
import ua.gradsoft.jungle.persistence.ejbqlao.EjbQlAccessObject;
import ua.gradsoft.jungle.persistence.jdbcex.RuntimeSqlException;
import ua.gradsoft.jungle.persistence.jpaex.JdbcConnectionWrapper;
import ua.gradsoft.jungle.persistence.jpaex.JpaEntityProperty;
import ua.gradsoft.jungle.persistence.jpaex.JpaEx;
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


    public  List<List<String>>   translateTableFieldsByIds(String language,
                                                          String tableName,
                                                          List<Object> ids,
                                                          List<String> columnNames)
    {
        return translateTableFieldsByIds1(language,tableName,ids,columnNames);
    }


    public<T> List<List<String>>   translateTableFieldsByIds1(String language,
                                                          String tableName,
                                                          List<Object> ids,
                                                          List<String> columnNames)
    {
      TranslationTable tt = getEntityManager().find(TranslationTable.class, tableName);
      if (tt==null) {
          throw new IllegalArgumentException("Can't find translation table for table "+tableName);
      }
      EntityCacheEntry<T> e = getOrCreateEntityCacheEntry(tt.getEntityClassName());

      List<JpaEntityProperty<? super T,String>> properties = new ArrayList<JpaEntityProperty<? super T,String>>();
      for(String columnName: columnNames) {
          JpaEntityProperty<T,String> p = JpaEntityProperty.<T,String>findByColumnName(e.entityClass, columnName);
          if (p==null) {
              throw new IllegalArgumentException("Can't find property for column "+columnName);
          }
          properties.add(p);
      }
      return translateFieldsByIds(language,e,ids,properties);
    }


    public List<String> translateTableFieldsById(String language, String tableName,
                                           Object id, List<String> columnNames)
    {
       return translateTableFieldsByIds(language,tableName,
                                       Collections.singletonList(id),columnNames).get(0); 
    }

    <T> List<List<String>>   translateFieldsByIds(String language,
                                              EntityCacheEntry e,
                                              List<Object> ids,
                                              List<JpaEntityProperty<? super T,String>> fields)
    {

      if (e.tt==null) {
          // impossible, so log and throw exception.
          throw new IllegalArgumentException("call of translateFieldsByIds with null transaltedTable for class "+e.entityClass.getName());
      }  
      language = language.toUpperCase();

      BundleInfo bi = e.tt.getBundle();
      boolean translate = !(bi.getPrimaryLanguage().getCode().equals("language"));
      if (translate) {
          checkLanguageIsSupported(bi,language);
      }
     
      List<List<String>> retval = new ArrayList<List<String>>();
      if (!translate) {
          for(Object id: ids) {
            T entity = (T)getEntityManager().find(e.entityClass, id);
            List<String> row = new ArrayList<String>();
            for(JpaEntityProperty<? super T,String> p: fields) {
              row.add(p.getValue(entity));
            }
            retval.add(row);
          }
      }else{
          Cache dataCache = getDataCache();
          String translatedTable = e.tt.getTranslationTableName();
          StringBuilder sqlBuilder = new StringBuilder();
          sqlBuilder.append("select ");
          sqlBuilder.append(e.tt.getPkColumnName());
          for(JpaEntityProperty<? super T,String> p: fields) {
              sqlBuilder.append(", ");
              String columnName =  getTranslatedColumnName(p.getColumnName(),language);
              sqlBuilder.append(columnName);
          }
          sqlBuilder.append(" from ");
          sqlBuilder.append(translatedTable);
          sqlBuilder.append(" where ");
          sqlBuilder.append(e.tt.getPkColumnName());
          boolean doHack = false; // actually we need check databases here.

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
                          List<String> row = getCachedTranslation(id,e.entityClass,language, fields);
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
                            T entity = (T)getEntityManager().find(e.entityClass, id);
                            for(JpaEntityProperty<? super T,String> p: fields) {
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
                          putCachedTranslation(id,e.entityClass,language,fields,retvalRow);
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
                      List<String> row = getCachedTranslation(id,e.entityClass,language,fields);
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
                      for(int i=0; i<fields.size(); ++i) {
                          String value = rs.getString(i+2);
                          retRow.add(value);
                      }
                      putCachedTranslation(rsid,e.entityClass,language,fields,retRow);
                    }
                  }
                  // now fill cached or untranslated parts
                  int i=0;
                  for(List<String> row: retval) {
                     if (row.size()==0) {
                          List<String> crow = getCachedTranslation(ids.get(i),e.entityClass,language,fields);
                          if (crow!=null) {
                              row.addAll(crow);
                          } else {
                              T entity = (T)getEntityManager().find(e.entityClass, ids.get(i));
                              for(JpaEntityProperty<? super T,String> p : fields) {
                                row.add(p.getValue(entity));
                              }
                          }
                     }
                     ++i;
                  }

              }
          }catch(SQLException ex){
              LOG.error("SQLException raised, code="+ex.getErrorCode());
              SQLException currex = ex;
              while(currex!=null) {
                  LOG.error(currex.getMessage());
                  currex=currex.getNextException();
              }
              throw new RuntimeSqlException(ex);
          }finally{
              wr.releaseConnection(cn);
          }

      }

      return retval;
    }


    public<T>  T  translateBean(T bean, String languageCode, boolean deep)
    { return translateBean(bean,languageCode, deep, false); }

    public<T>  T  translateBean(T bean, String languageCode, boolean deep, boolean detached)
    {
      System.err.println("translateBean("+bean.toString()+","+languageCode+","+deep+")");
      if (bean==null) {
          return null;
      }
      if (!detached) {
          bean = JpaEx.serializeAndDeserialize(bean);
      }
      if (bean instanceof Collection) {
          return (T)translateBeans((Collection)bean,languageCode, deep, true);
      }else if (bean instanceof Map) {
          Map<Object,Object> m = (Map<Object,Object>)bean;
          for(Map.Entry<Object,Object> e: m.entrySet()) {
              Object tv = translateBean(e.getValue(),languageCode, deep, true);
              m.put(e.getKey(), tv);
          }
          return (T)m;
      }else if (bean.getClass().isAnnotationPresent(Entity.class)) {
          Collection<T> rb = translateBeans(Collections.<T>singletonList(bean),languageCode,deep, true);
          return rb.iterator().next();
      }else {
          // return unchanged
          return bean;
      }
    }

    public<T> Collection<T>  translateBeans(Collection<T> beans, String languageCode, boolean deep)
    {
      return translateBeans(beans,languageCode, deep, false);
    }


    /**
     * translate beans to choosen language. (detach ones if needed)
     */
    public<T> Collection<T>  translateBeans(Collection<T> beans, String languageCode, boolean deep, boolean detached)
    {
        System.err.println("call of translateBeans");
        if (beans==null) {
            return beans;
        }
        if (!detached) {
            beans = JpaEx.<Collection<T>>serializeAndDeserialize(beans);
        }
        Iterator<T> it = beans.iterator();
        if (!it.hasNext()) {
            // collection is empty, return unchanged.
            return beans;
        }
        String lc = languageCode.toUpperCase();
        Class<T> entityClass;
        {
         T bean = it.next();
         entityClass = JpaHelper.findSameOrSuperJpaEntity(bean.getClass());
         if (entityClass==null) {
            // "atempt to translate not-entity class"
            return beans;
         }
        }
        EntityCacheEntry<T> metaDataEntry = getOrCreateEntityCacheEntry(entityClass.getName());

        if (metaDataEntry==null) {
            throw new IllegalArgumentException("class "+entityClass+" can;t be translated (translation metainfo not found)");
        }


        Map<String,BundleInfo> bis = new TreeMap<String, BundleInfo>();
        for(JpaEntityProperty<T,String> p: metaDataEntry.stringPropertiesByName.values()) {
            String poName = p.getEntityClass().getName();
            EntityCacheEntry poEntry = getOrCreateEntityCacheEntry(poName);
            if (!bis.containsKey(poName)) {
                if (poEntry.tt!=null) {
                    BundleInfo bi=poEntry.tt.getBundle();
                    boolean supportedLanguageFound = false;
                    for(LanguageInfo li: bi.getSupportedLanguages()) {
                        if (li.getCode().equalsIgnoreCase(languageCode)) {
                            supportedLanguageFound=true;
                            break;
                        }
                    }
                    if (!supportedLanguageFound) {
                        throw new IllegalArgumentException("Language "+languageCode+" is not supported");
                    }
                    bis.put(poName, bi);
                }
            }
        }


        // now prepare list of ids
        List<Object> ids = new ArrayList<Object>();
        for(T bean: beans) {
            ids.add(metaDataEntry.idProperty.getValue(bean));
        }

        Map<String,List<List<String>>> translatedPerClass = new TreeMap<String, List<List<String>>>();
        Map<String,List<JpaEntityProperty<? super T,String>>> propertiesPerClass = new TreeMap<String,List<JpaEntityProperty<? super T,String>>>();
        for(Map.Entry<String,EntityCacheEntry<? super T>> e: metaDataEntry.slicedEntries.entrySet()) {
          TranslationTable tt = e.getValue().tt;
          if (tt==null) continue;
          List<JpaEntityProperty<? super T,String>> fieldProperties = new ArrayList<JpaEntityProperty<? super T,String>>();
          for(TranslationTableColumn tc: tt.getTranslatedColumns()) {
            JpaEntityProperty<? super T,String> fieldProperty  = e.getValue().stringPropertiesByNormalizedColumnName.get(tc.getColumnName());
            if (fieldProperty==null) {
                //impossible
                throw new IllegalStateException("can;t find property for column "+tc.getColumnName());
            }
            fieldProperties.add(fieldProperty);
          }

          List<List<String>> translatedForKey =  translateFieldsByIds(languageCode,
                                                                       e.getValue(),
                                                                       ids,
                                                                       fieldProperties);

          translatedPerClass.put(e.getKey(), translatedForKey);
          propertiesPerClass.put(e.getKey(), fieldProperties);

        }

        System.err.println("now translate beans to "+languageCode);

        it = beans.iterator();
        for(int i=0; it.hasNext() ;++i) {
          T bean = it.next();
          for(Map.Entry<String,List<List<String>>> e: translatedPerClass.entrySet()) {
            if (e.getValue().size()==0) {
                continue;
            }
            List<String> translatedRow = e.getValue().get(i);
            List<JpaEntityProperty<? super T,String>> fieldProperties =  propertiesPerClass.get(e.getKey());

            for(int j=0; j<fieldProperties.size(); ++j) {
                System.err.println("set area for property "+fieldProperties.get(j).getName()+", translation="+translatedRow.get(j));
                fieldProperties.get(j).setValue(bean,translatedRow.get(j));
            }
          }


          // now translate non-trivial complex properties
          if (deep) {
            System.err.println("translate intenal beans:");
            for(JpaEntityProperty p: JpaHelper.getAllJpaProperties(entityClass)) {
                System.err.print("check for property with name "+p.getName());
                Class propertyClass = p.getPropertyClass();
                if (!propertyClass.isPrimitive() &&
                    !Number.class.isAssignableFrom(propertyClass) &&
                    !String.class.isAssignableFrom(propertyClass)) {
                  Object v = p.getValue(bean);
                  if (v!=null) {
                    System.err.println("translate bean for "+v.toString());
                    Object tv = translateBean(v,languageCode,deep, true);
                    p.setValue(bean, tv);
                  }
                }
            }
            System.err.println("intenal beans translate end:");
          }

        }
        System.err.println("ok, all translated");

        return beans;

    }


    
    private String getTranslatedColumnName(String originColumnName, String language)
    {
        return normalizeColumnName(originColumnName)+"_"+language;
    }

    private String normalizeColumnName(final String columnName)
    {
      String name = columnName;
      if (name.endsWith("_eng")||name.endsWith("_ENG")) {
          name=name.substring(0,name.length()-4);
      }else if (name.endsWith("_en")||name.endsWith("_EN")) {
          name=name.substring(0, name.length()-3);
      }
      return name;
    }

    private<T> EntityCacheEntry<T> getOrCreateEntityCacheEntry(String entityClassName)
    {
     Cache metadataCache = getMetadataCache();
     Element e = metadataCache.get("E_"+entityClassName);
     if (e!=null) {
         return (EntityCacheEntry)e.getObjectValue();
     }else{
         EntityCacheEntry entry = new EntityCacheEntry();
         entry.tt = getTranslationTable(entityClassName);
         try {
           entry.entityClass=Class.forName(entityClassName);
         } catch (ClassNotFoundException ex){
             throw new IllegalStateException("Class "+entityClassName+" is not found");
         }
         entry.slicedEntries = new TreeMap<String,EntityCacheEntry<? super T>>();
         entry.slicedEntries.put(entityClassName, entry);
         fillEntityEntry(entry);
         if (entry.tt==null && entry.slicedEntries.size()==1) {
             return null;
         }
         metadataCache.put(new Element("E_"+entityClassName,entry));
         return entry;
     }
    }
    
    private TranslationTable getTranslationTable(String entityClassName)
    {
      List<TranslationTable> ltt;
      //System.err.println("base property class is "+entityClassName+" , propery="+property.getName());
      ltt = executeQuery(TranslationTable.class,
                    "select tt from TranslationTable tt \n"+
                                 " where tt.entityClassName=:name",
                             Collections.<String,Object>singletonMap("name", entityClassName),
                             Collections.<String,Object>emptyMap());
      if (ltt.size()==0) {
          if (LOG.isDebugEnabled()) {
              LOG.debug("translation table for property class "+entityClassName+" is not found");
          }
          //System.err.println("translation table for property class "+entityClassName+" is not found");
          return null;
      }
      TranslationTable tt = ltt.get(0);
      return tt;
    }

    private<T> void fillEntityEntry(EntityCacheEntry<T> entry)
    {
      List<JpaEntityProperty> properties=JpaHelper.getAllJpaProperties(entry.entityClass);
      entry.stringPropertiesByNormalizedColumnName=new TreeMap<String,JpaEntityProperty<T,String>>();
      entry.stringPropertiesByName = new TreeMap<String,JpaEntityProperty<T,String>>();
      System.err.println("fillEntityEntry, class = "+entry.entityClass.getName());
      for(JpaEntityProperty p: properties) {
          boolean used=false;
          if (p.getPropertyClass().isAssignableFrom(String.class)) {
              System.err.println("property, name="+p.getName()+", columnName="+p.getColumnName());
              entry.stringPropertiesByNormalizedColumnName.put(normalizeColumnName(p.getColumnName()), p);
              entry.stringPropertiesByName.put(p.getName(), p);
              used=true;
          } else if (p.isId()) {
              entry.idProperty = p;
          }
          if (used) {
            Class propertyOwnerClass = p.getEntityClass();
            if (!entry.entityClass.equals(propertyOwnerClass)) {
                String propertyOwnerClassName = propertyOwnerClass.getName();
                if (!entry.slicedEntries.containsKey(propertyOwnerClassName)) {
                    EntityCacheEntry propertyOwnerCacheEntry = getOrCreateEntityCacheEntry(propertyOwnerClassName);
                    if (propertyOwnerCacheEntry!=null) {
                      entry.slicedEntries.put(propertyOwnerClassName, propertyOwnerCacheEntry);
                    }
                }
            }
          }
      }
    }

 
    private void checkLanguageIsSupported(BundleInfo bi, String language)
    {
      for(LanguageInfo li: bi.getSupportedLanguages()) {
          if (li.getCode().equalsIgnoreCase(language)) {
              return;
          }
      }  
      throw new IllegalArgumentException("language "+language+" is not supported for bundle "+bi.getName());
    }

    private<T> List<String> getCachedTranslation(Object id, Class<T> entity, String language, List<JpaEntityProperty<? super T,String>> fields)
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
        for(JpaEntityProperty p: fields) {
            String value = hs.get(p.getName());
            if (value==null) {
                return null;
            }
            retval.add(value);
        }
        return retval;
    }

    public<T> void putCachedTranslation(Object id, Class<T> entityClass, String language, List<JpaEntityProperty<? super T,String>> fields, List<String> values)
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
       for(int i=0; i<fields.size(); ++i) {
           hs.put(fields.get(i).getName(), values.get(i));
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

    static class EntityCacheEntry<T> implements Serializable
    {
      public TranslationTable tt;
      public Map<String,EntityCacheEntry<? super T>> slicedEntries;
      public Map<String,JpaEntityProperty<T,String>> stringPropertiesByName;
      public Map<String,JpaEntityProperty<T,String>> stringPropertiesByNormalizedColumnName;
      public JpaEntityProperty idProperty;
      public String bundleName;
      public Class<T> entityClass;
    }

    public EntityManager getEntityManager()
    {
      return en_;  
    }

    private static String LOCALIZATION_METADATA_CACHE = "_LOCALIZATION_MD";
    private static String LOCALIZATION_DATA_CACHE = "_LOCALIZATION_DATA";
    private static final Log LOG = LogFactory.getLog(LocalizationFacadeImpl.class);

    @PersistenceContext
    EntityManager en_;
}
