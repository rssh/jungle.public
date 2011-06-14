
package ua.gradsoft.hibernateplugin.ritree;

import java.io.Serializable;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Comparator;
import java.util.Map;
import java.util.Set;
import java.util.HashSet;
import org.hibernate.AssertionFailure;
import org.hibernate.EntityMode;
import org.hibernate.LockOptions;
import org.hibernate.FetchMode;
import org.hibernate.HibernateException;
import org.hibernate.LockMode;
import org.hibernate.MappingException;
import org.hibernate.QueryException;
import org.hibernate.cache.access.EntityRegionAccessStrategy;
import org.hibernate.cache.entry.CacheEntryStructure;
import org.hibernate.engine.CascadeStyle;
import org.hibernate.engine.SessionFactoryImplementor;
import org.hibernate.engine.SessionImplementor;
import org.hibernate.engine.ValueInclusion;
import org.hibernate.id.IdentifierGenerator;
import org.hibernate.loader.entity.UniqueEntityLoader;
import org.hibernate.mapping.PersistentClass;
import org.hibernate.metadata.ClassMetadata;
import org.hibernate.persister.entity.EntityPersister;
import org.hibernate.persister.entity.Joinable;
import org.hibernate.persister.entity.Loadable;
import org.hibernate.persister.entity.NamedQueryLoader;
import org.hibernate.persister.entity.OuterJoinLoadable;
import org.hibernate.persister.entity.Queryable;
import org.hibernate.persister.entity.DiscriminatorMetadata;
import org.hibernate.sql.Alias;
import org.hibernate.sql.SelectFragment;
import org.hibernate.tuple.StandardProperty;
import org.hibernate.tuple.entity.EntityMetamodel;
import org.hibernate.tuple.entity.EntityTuplizer;
import org.hibernate.type.EntityType;
import org.hibernate.type.Type;
import org.hibernate.type.VersionType;
import org.hibernate.util.StringHelper;

/**
 *
 * @author rssh
 */
public class RiTreeFunPersister implements EntityPersister,
                                 OuterJoinLoadable, Queryable
                                /*extends AbstractEntityPersister */
             
{

    RiTreeFunPersister(String funName, PersistentClass persistentClass,
                        EntityRegionAccessStrategy cacheAccessStrategy,
                        SessionFactoryImplementor factory)
    {
        funName_ = funName;
        Class userClass = persistentClass.getMappedClass();
        entityMetamodel_ = new EntityMetamodel(persistentClass, factory);
        persistentClass_ = persistentClass;

        factory_ = factory;
        cacheAccessStrategy_ = cacheAccessStrategy;

        //NLP here
        //classMetadata_ = factory.getClassMetadata(RiTreeInterval.class);
        classMetadata_ = new RiTreeIntervalClassMetadata(userClass.getSimpleName(), userClass, entityMetamodel_);
        //classMetadata_ = null;

        //keyColumnNames_ = KEY_COLUMN_NAMES;
      
        fakeTableName_=funName_+"(:ri.bottom,:ri.top)";

    }

    public void afterInitialize(Object entity, boolean lazyUnfetched, SessionImplementor session) {
        getTuplizer(session.getEntityMode()).afterInitialize(entity, lazyUnfetched, session);
    }

    public void afterReassociate(Object arg0, SessionImplementor arg1) {
        /* do nothing */
    }

    public boolean canExtractIdOutOfEntity() {
        return false;
    }

    public Object createProxy(Serializable arg0, SessionImplementor arg1) throws HibernateException {
        throw new HibernateException("RiFunction objects does not have proxies");
    }

    public void delete(Serializable arg0, Object arg1, Object arg2, SessionImplementor arg3) throws HibernateException {
        throw new HibernateException("Functional object can't be deleted");
    }

    /**
     * functional objects is immutable.
     *@return null
     */
    public int[] findDirty(Object[] arg0, Object[] arg1, Object arg2, SessionImplementor arg3) {
        return null;
    }

    /**
     * @return null
     */
    public int[] findModified(Object[] old, Object[] current, Object entity, SessionImplementor session) {
        return null;
    }

    public Object forceVersionIncrement(Serializable arg0, Object arg1, SessionImplementor arg2) throws HibernateException {
        throw new AssertionFailure("RiTreeFun is not versioning");
    }

    public EntityRegionAccessStrategy getCacheAccessStrategy() {
        return cacheAccessStrategy_;
    }

    public CacheEntryStructure getCacheEntryStructure() {
        return null;
    }

    public ClassMetadata getClassMetadata() {
        return classMetadata_;
    }

    public Class getConcreteProxyClass(EntityMode arg0) {
        return null;
    }

    public Object getCurrentVersion(Serializable id, SessionImplementor session) throws HibernateException {
        // TODO: check sql ?
        // we assume that collections used only inside query..
        return null;
    }

    public Object[] getDatabaseSnapshot(Serializable id, SessionImplementor session) throws HibernateException {
        return null;
    }

    public EntityMetamodel getEntityMetamodel() {
        return entityMetamodel_;
    }

    public String getEntityName() {
        return entityMetamodel_.getEntityType().getName();
    }

    public SessionFactoryImplementor getFactory() {
        return factory_;
    }

    @Deprecated
    public Serializable getIdentifier(Object arg0, EntityMode arg1) throws HibernateException {
        return ((RiTreeInterval)arg0).getPk();
    }

    public Serializable getIdentifier(Object arg0, SessionImplementor arg1) throws HibernateException {
        return ((RiTreeInterval)arg0).getPk();
    }

    public IdentifierGenerator getIdentifierGenerator() {
       return null;
    }

    public String getIdentifierPropertyName() {
       return entityMetamodel_.getIdentifierProperty().getName();
    }

    public Type getIdentifierType() {
        return entityMetamodel_.getIdentifierProperty().getType();
    }

    public Class getMappedClass(EntityMode entityMode) {
        EntityTuplizer tup = entityMetamodel_.getTuplizerOrNull(entityMode);
        return (tup==null ? null : tup.getMappedClass());
    }

    public int[] getNaturalIdentifierProperties() {
        return entityMetamodel_.getNaturalIdentifierProperties();
    }

    public Object[] getNaturalIdentifierSnapshot(Serializable arg0, SessionImplementor arg1) {
        return null;
    }

    public CascadeStyle[] getPropertyCascadeStyles() {
        return entityMetamodel_.getCascadeStyles();
    }

    public boolean[] getPropertyCheckability() {
        return entityMetamodel_.getPropertyCheckability();
    }

    public ValueInclusion[] getPropertyInsertGenerationInclusions() {
        return entityMetamodel_.getPropertyInsertGenerationInclusions();
    }

    public boolean[] getPropertyInsertability() {
        return entityMetamodel_.getPropertyInsertability();
    }

    public boolean[] getPropertyLaziness() {
        return entityMetamodel_.getPropertyLaziness();
    }

    public String[] getPropertyNames() {
        return entityMetamodel_.getPropertyNames();
    }

    public boolean[] getPropertyNullability() {
        return entityMetamodel_.getPropertyNullability();
    }

    public Serializable[] getPropertySpaces() {
        String[] retval = {};
        return retval;
    }

    public Type getPropertyType(String name) throws MappingException {
        int index = entityMetamodel_.getPropertyIndex(name);
        return entityMetamodel_.getProperties()[index].getType();
    }

    public Type[] getPropertyTypes() {

        return entityMetamodel_.getPropertyTypes();
    }

    public ValueInclusion[] getPropertyUpdateGenerationInclusions() {
        return entityMetamodel_.getPropertyUpdateGenerationInclusions();
    }

    public boolean[] getPropertyUpdateability() {
        return entityMetamodel_.getPropertyUpdateability();
    }

    public Object getPropertyValue(Object object, int i, EntityMode entityMode) throws HibernateException {
        return getTuplizer(entityMode).getPropertyValue(object, i);
    }

    public Object getPropertyValue(Object object, String name, EntityMode entityMode) throws HibernateException {
        return getTuplizer(entityMode).getPropertyValue(object, name);
    }

    public Object[] getPropertyValues(Object object, EntityMode entityMode) throws HibernateException {
        return getTuplizer(entityMode).getPropertyValues(object);
    }

    public Object[] getPropertyValuesToInsert(Object object, Map mergeMap, SessionImplementor session) throws HibernateException {
        return getTuplizer(session.getEntityMode()).getPropertyValuesToInsert(object, mergeMap, session);
    }

    public boolean[] getPropertyVersionability() {
        return entityMetamodel_.getPropertyVersionability();
    }

    public Serializable[] getQuerySpaces() {
        return getPropertySpaces();
    }

    public String getRootEntityName() {
        return entityMetamodel_.getRootName();
    }

    public EntityPersister getSubclassEntityPersister(Object arg0, SessionFactoryImplementor arg1, EntityMode arg2) {
        // we have no subclasses.
        return this;
    }

    public Object getVersion(Object object, EntityMode entityMode) throws HibernateException {
        return null;
    }

    public int getVersionProperty() {
        return -1;
    }

    public VersionType getVersionType() {
        return null;
    }

    public EntityMode guessEntityMode(Object object) {
        return entityMetamodel_.guessEntityMode(object);
    }

    public boolean hasCache() {
        return false;
    }

    public boolean hasCascades() {
        return entityMetamodel_.hasCascades();
    }

    public boolean hasCollections() {
        return entityMetamodel_.hasCollections();
    }

    public boolean hasIdentifierProperty() {
        return !entityMetamodel_.getIdentifierProperty().isVirtual();
    }

    public boolean hasInsertGeneratedProperties() {
        // we have no inserta at all
        return false;
    }

    public boolean hasLazyProperties() {
        return entityMetamodel_.hasLazyProperties();
    }

    public boolean hasMutableProperties() {
        // our stuff is immutable.
        return false;
    }

    public boolean hasNaturalIdentifier() {
        return entityMetamodel_.hasNaturalIdentifier();
    }

    public boolean hasProxy() {
        return entityMetamodel_.isLazy();
    }

    public boolean hasSubselectLoadableCollections() {
        return false;
    }

    public boolean hasUninitializedLazyProperties(Object object, EntityMode entityMode) {
        //return getTuplizer(entityMode).hasUninitializedLazyProperties(object);
        return false;
    }

    public boolean hasUpdateGeneratedProperties() {
        // we have no updates/
        return false;
    }

    public boolean implementsLifecycle(EntityMode entityModel) {
        return getTuplizer(entityModel).isLifecycleImplementor();
    }

    public boolean implementsValidatable(EntityMode entityModel) {
        return getTuplizer(entityModel).isValidatableImplementor();
    }

    public void insert(Serializable id, Object[] fields, Object object, SessionImplementor arg3) throws HibernateException {
        throw new AssertionFailure("FunObjects does not have inserts");
    }

    public Serializable insert(Object[] arg0, Object arg1, SessionImplementor arg2) throws HibernateException {
        throw new AssertionFailure("FunObjects does not have inserts");
    }

   
    public Object instantiate(Serializable id, SessionImplementor session) throws HibernateException {
        return getTuplizer(session).instantiate(id,session);
    }
  
    @Deprecated
    public Object instantiate(Serializable id, EntityMode entityMode) throws HibernateException {
        return getTuplizer(entityMode).instantiate(id);
    }

    public boolean isBatchLoadable() {
        return true;
    }

    public boolean isCacheInvalidationRequired() {
        return false;
    }

    public boolean isIdentifierAssignedByInsert() {
        return true;
    }

    public boolean isInherited() {
        return entityMetamodel_.isInherited();
    }

    public boolean isInstance(Object object, EntityMode entityMode) {
        return getTuplizer(entityMode).isInstance(object);
    }

    public boolean isInstrumented(EntityMode entityMode) {
        EntityTuplizer tuplizer = entityMetamodel_.getTuplizerOrNull(entityMode);
        return tuplizer!=null && tuplizer.isInstrumented();
    }


    public boolean isLazyPropertiesCacheable() {
       // we have no lazy properties at all.
        return false;
    }

    public boolean isMutable() {
        return false;
    }

    public boolean isSelectBeforeUpdateRequired() {
        return false;
    }

    public boolean isSubclassEntityName(String arg0) {
        return entityMetamodel_.getSubclassEntityNames().contains(arg0);
    }

    public Boolean isTransient(Object arg0, SessionImplementor arg1) throws HibernateException {
        return true;
    }

    public boolean isVersionPropertyGenerated() {
        return false;
    }

    public boolean isVersioned() {
        return false;
    }

    @Deprecated
    public Object load(Serializable id, Object optionalObject, LockMode lockModel, SessionImplementor session) throws HibernateException {
        if (queryLoader_==null) {
            queryLoader_ = new NamedQueryLoader(persistentClass_.getLoaderName(),this);
        }
        return queryLoader_.load(id, optionalObject, session);
    }

   public Object load(Serializable id, Object optionalObject, LockOptions lockOptions, SessionImplementor session) throws HibernateException {
        if (queryLoader_==null) {
            queryLoader_ = new NamedQueryLoader(persistentClass_.getLoaderName(),this);
        }
        return queryLoader_.load(id, optionalObject, session, lockOptions);
    }




    public void lock(Serializable id, Object version, Object object, LockMode lockMode, SessionImplementor session) throws HibernateException {
       /* do nothing: we have no locks */
    }

    public void lock(Serializable id, Object version, Object object, LockOptions lockOptions, SessionImplementor session) throws HibernateException {
       /* do nothing: we have no locks */
    }


    public void postInstantiate() throws MappingException {
    
    }

    public void processInsertGeneratedProperties(Serializable arg0, Object arg1, Object[] arg2, SessionImplementor arg3) {
       
    }

    public void processUpdateGeneratedProperties(Serializable arg0, Object arg1, Object[] arg2, SessionImplementor arg3) {
      
    }

    @Deprecated
    public void resetIdentifier(Object entity, Serializable currentId, Object currentVersion, EntityMode entityMode) {
        getTuplizer(entityMode).resetIdentifier(entity, currentId, currentVersion);
    }

    public void resetIdentifier(Object entity, Serializable currentId, Object currentVersion, SessionImplementor session) {
        getTuplizer(session).resetIdentifier(entity, currentId, currentVersion, session);
    }

    @Override
    public void setIdentifier(Object entity, Serializable id, SessionImplementor session) throws HibernateException {
        getTuplizer(session).setIdentifier(entity, id,session);
    }


    @Deprecated
    public void setIdentifier(Object entity, Serializable id, EntityMode entityMode) throws HibernateException {
        getTuplizer(entityMode).setIdentifier(entity, id);
    }

    public void setPropertyValue(Object object, int i, Object value, EntityMode entityMode) throws HibernateException {
        getTuplizer(entityMode).setPropertyValue(object, i, value);
    }

    public void setPropertyValues(Object object, Object[] values, EntityMode entityMode) throws HibernateException {
        getTuplizer(entityMode).setPropertyValues(object, values);
    }

    public void update(Serializable arg0, Object[] arg1, int[] arg2, boolean arg3, Object[] arg4, Object arg5, Object arg6, Object arg7, SessionImplementor arg8) throws HibernateException {
        throw new AssertionFailure("Object is immutable");
    }

    public Comparator getVersionComparator() {
        return null;
    }

    // joinable


    // ver very very ugly
    public boolean consumesCollectionAlias() {
        return false;
    }

    public boolean consumesEntityAlias() {
        return true;
    }

    public String filterFragment(String alias, Map enabledFilters) throws MappingException {
        // all what needed is in generated fakeTableName.
        //so, filter itself will be empty
        return "";
    }

    public String fromJoinFragment(String alias, boolean innerJoin, boolean includeSubclasses) {
        // this is 'all otjer' joins.
        return "";
    }

    public String[] getKeyColumnNames() {
        return KEY_COLUMN_NAMES;
    }

    public String getName() {
        return getEntityName();
    }

    public String getTableName() {
        return fakeTableName_;
    }

    public boolean isCollection() {
        return false;
    }

    public String oneToManyFilterFragment(String arg0) throws MappingException {
        return "";
    }

    public String selectFragment(Joinable rhs, String rhsAlias, String lhsAlias,
                                 String entitySuffix, String collectionSuffix,
                                 boolean includeCollectionColumns) {
        return selectFragment(lhsAlias, entitySuffix);
    }


    public String whereJoinFragment(String alias, boolean innerJoin, boolean includeSubclasses) {
        //it;s join by tables in where. Since we have only one table, our join fragment is empty.
        return "";
    }

    // OuterJoinLoadable

    public EntityType getEntityType() {
        return entityMetamodel_.getEntityType();
    }

    public int countSubclassProperties() {
        return 0;
    }

    public String fromTableFragment(String name) {
        return fakeTableName_+" "+name;
    }

    public CascadeStyle getCascadeStyle(int arg0) {
        return CascadeStyle.NONE;
    }

    public FetchMode getFetchMode(int arg0) {
        return FetchMode.DEFAULT;
    }

    public String[] getPropertyColumnNames(String propertyPath) {
        String[] retval=null;
        if (propertyPath.equals("interval")||
            propertyPath.equals(EntityPersister.ENTITY_ID)) {
            retval=KEY_COLUMN_NAMES;
        }else if (propertyPath.equals("interval.begin")
            ||propertyPath.equals("id.begin")) {
            retval=INTERVAL_BEGIN_COLUMN_NAMES;
        }else if (propertyPath.equals("interval.end")
                ||propertyPath.equals("id.end")) {
            retval=INTERVAL_END_COLUMN_NAMES;
        }else{
            throw new HibernateException("Can't handle propertyPath "+propertyPath);
        }
        return retval;
    }

    public String getPropertyTableName(String propertyName) {
        return getTableName();
    }


    public String[] getSubclassPropertyColumnNames(int index) {
        switch(index) {
            case 0:
                return KEY_COLUMN_NAMES;
            case 1:
                return INTERVAL_BEGIN_COLUMN_NAMES;
            case 2:
                return INTERVAL_END_COLUMN_NAMES;
            default:
                throw new HibernateException("Invalid property index:"+index);
        }
    }

    public String getSubclassPropertyName(int index) {
        return getClassMetadata().getPropertyNames()[index];
        //return classMetadata_.getPropertyNames()[index];
    }

    public String getSubclassPropertyTableName(int arg0) {
        return fakeTableName_;
    }

    public Type getSubclassPropertyType(int index) {
        return classMetadata_.getPropertyTypes()[index];
    }

    public boolean isDefinedOnSubclass(int arg0) {
        return false;
    }

    public boolean isSubclassPropertyNullable(int index) {
        return classMetadata_.getPropertyNullability()[index];
    }

    public String selectFragment(String alias, String suffix) {
            String s1= new SelectFragment()
                           .setSuffix(suffix)
                           .addColumns(alias, KEY_COLUMN_NAMES, KEY_COLUMN_NAMES)
                           .toFragmentString().substring(2);
            return s1;
    }

    public String[] toColumns(String alias, int index) {
        String[] columnNames = getSubclassPropertyColumnNames(index);
        String[] retval = new String[columnNames.length];
        for(int i=0; i<retval.length; ++i) {
            retval[i]=StringHelper.qualify(alias, columnNames[i]);
        }
       return retval;
    }

    public String getDiscriminatorAlias(String suffix) {
        return null;
    }

    public String getDiscriminatorColumnName() {
        return null;
    }

    public Type getDiscriminatorType() {
        if (persistentClass_.getDiscriminator()==null) {
           return null;
        } else {
           return persistentClass_.getDiscriminator().getType();
        }
    }

    public String[] getIdentifierAliases(String suffix)
    {
        return new Alias(suffix).toUnquotedAliasStrings(KEY_COLUMN_NAMES);
    }

    public String[] getIdentifierColumnNames() {
        return KEY_COLUMN_NAMES;
    }

    public String[] getPropertyAliases(String suffix, int i) {
        return new Alias(suffix).toUnquotedAliasStrings(getPropertyColumnNames(i));
    }

    public String[] getPropertyColumnNames(int arg0) {
        return this.getSubclassPropertyColumnNames(arg0);
    }

    public String getSubclassForDiscriminatorValue(Object arg0) {
        return null;
    }

    public boolean hasRowId() {
        return false;
    }

    public boolean hasSubclasses() {
        return false;
    }

    public Object[] hydrate(ResultSet rs, Serializable id, Object object,
                            Loadable rootLoadable, String[][] suffixedPropertyColumns,
                            boolean allProperties, SessionImplementor session) throws SQLException, HibernateException {
        Type[] types = getPropertyTypes();
        Object[] retval = new Object[types.length];
        for(int i=0; i<types.length; ++i) {
            String[] cols = suffixedPropertyColumns[i];
            retval[i] = types[i].hydrate(rs, cols, session, object);
        }
        return retval;
    }

    public boolean isAbstract() {
        return false;
    }

     // Querable


    public String generateFilterConditionAlias(String rootAlias) {
        return ""; // now we have not gloabl condition
    }

    public String[] getConstraintOrderedTableNameClosure() {
        return TABLE_NAMES;
    }

    public String[][] getContraintOrderedTableKeyColumnClosure() {
        String[][] retval = new String[1][];
        retval[0] = KEY_COLUMN_NAMES;
        return retval;
    }

    public String getDiscriminatorSQLValue() {
        return null;
    }

    public String getMappedSuperclass() {
        return null;
    }

    public Declarer getSubclassPropertyDeclarer(String arg0) {
        return Declarer.CLASS;
    }

    public int getSubclassPropertyTableNumber(String propertyPath) {
        return 0;
    }

    public String getSubclassTableName(int number) {
        if (number==0) {
            return null;
        } else {
            return null;
        }

    }

    public String getTemporaryIdTableDDL() {
        return persistentClass_.getTemporaryIdTableDDL();
    }

    public String getTemporaryIdTableName() {
        return persistentClass_.getTemporaryIdTableName();
    }

    public String identifierSelectFragment(String alias, String suffix) {
            String s1= new SelectFragment()
                           .setSuffix(suffix)
                           .addColumns(alias, KEY_COLUMN_NAMES, KEY_COLUMN_NAMES)
                           .toFragmentString().substring(2);
            return s1;
    }

    public boolean isExplicitPolymorphism() {
        return false;
    }

    public boolean isMultiTable() {
        return false;
    }

    public boolean isVersionPropertyInsertable() {
        return false;
    }
  

    public String propertySelectFragment(String alias, String suffix, boolean allProperties) {
         return propertySelectFragmentFragment(alias, suffix, allProperties).toFragmentString();
    }

    public SelectFragment propertySelectFragmentFragment(String alias, String suffix, boolean allProperties) {
         SelectFragment select = new SelectFragment()
                            .setSuffix(suffix)
                            .setUsedAliases(getIdentifierAliases(suffix));
         // and we have no non-used fields in this select
         //select.addColumns(aliases, KEY_COLUMN_NAMES, getIdentifierAliases(suffix));
         return select;
    }



    public Type getType() {
        return entityMetamodel_.getEntityType();
    }

    public String[] toColumns(String alias, String propertyName) throws QueryException {
        String [] frs = toColumns(propertyName);
        String [] retval = new String[frs.length];
        for(int i=0; i<retval.length; ++i) {
            retval[i]=StringHelper.qualify(alias, frs[i]);
        }
        return retval;
    }

    public String[] toColumns(String propertyName) throws QueryException, UnsupportedOperationException {
        return this.getPropertyColumnNames(propertyName);
    }

    public Type toType(String propertyName) throws QueryException {
        return classMetadata_.getPropertyType(propertyName);
    }

    public void registerAffectingFetchProfile(String fetchProfileName) {
                affectingFetchProfileNames.add( fetchProfileName );
    }

    public DiscriminatorMetadata getTypeDiscriminatorMetadata() 
    {
       return null;
    }


    protected EntityTuplizer getTuplizer(SessionImplementor session)
    {
        return entityMetamodel_.getTuplizer(session.getEntityMode());
    }

    protected EntityTuplizer getTuplizer(EntityMode entityMode)
    {
        return entityMetamodel_.getTuplizer(entityMode);
    }

    private final String funName_;
    private final ClassMetadata classMetadata_;
    private final EntityMetamodel entityMetamodel_;
    private final SessionFactoryImplementor  factory_;
    private  UniqueEntityLoader queryLoader_=null;
    private final EntityRegionAccessStrategy cacheAccessStrategy_;
    private final PersistentClass  persistentClass_;
   // private final BasicEntityPropertyMapping propertyMapping_;
   // private final String[]  keyColumnNames_;
    private final String    fakeTableName_;

    private final Set affectingFetchProfileNames = new HashSet();


    static final String[] KEY_COLUMN_NAMES =  { "lower", "upper" };
    static final String[] INTERVAL_BEGIN_COLUMN_NAMES =  { "lower" };
    static final String[] INTERVAL_END_COLUMN_NAMES =  { "upper" };

    static final String[] TABLE_NAMES = { "ri_tree.ri_tree" };

}
