
package ua.gradsoft.hibernateplugin.ritree;

import java.io.Serializable;
import java.util.Comparator;
import java.util.Map;
import org.hibernate.AssertionFailure;
import org.hibernate.EntityMode;
import org.hibernate.HibernateException;
import org.hibernate.LockMode;
import org.hibernate.MappingException;
import org.hibernate.cache.access.EntityRegionAccessStrategy;
import org.hibernate.cache.entry.CacheEntryStructure;
import org.hibernate.engine.CascadeStyle;
import org.hibernate.engine.SessionFactoryImplementor;
import org.hibernate.engine.SessionImplementor;
import org.hibernate.engine.ValueInclusion;
import org.hibernate.id.IdentifierGenerator;
import org.hibernate.mapping.PersistentClass;
import org.hibernate.metadata.ClassMetadata;
import org.hibernate.persister.entity.BasicEntityPropertyMapping;
import org.hibernate.persister.entity.EntityPersister;
import org.hibernate.persister.entity.Joinable;
import org.hibernate.persister.entity.OuterJoinLoadable;
import org.hibernate.tuple.entity.EntityMetamodel;
import org.hibernate.tuple.entity.EntityTuplizer;
import org.hibernate.type.Type;
import org.hibernate.type.VersionType;

/**
 *
 * @author rssh
 */
public class RiTreeFunPersister1 implements EntityPersister
                                 // OuterJoinLoadable
{

    RiTreeFunPersister1(String funName, PersistentClass persistentClass,
                        EntityRegionAccessStrategy cacheAccessStrategy,
                        SessionFactoryImplementor factory)
    {
        funName_ = funName;
        Class userClass = persistentClass.getMappedClass();
        entityMetamodel_ = new EntityMetamodel(persistentClass, factory);
        classMetadata_ = new RiTreeIntervalClassMetadata(userClass.getSimpleName(), userClass);
        factory_ = factory;
        cacheAccessStrategy_ = cacheAccessStrategy;
        //propertyMapping_ = new BasicEntityPropertyMapping( this );
        propertyMapping_ = null;
        keyColumnNames_ = KEY_COLUMN_NAMES;
    }

    public void afterInitialize(Object arg0, boolean arg1, SessionImplementor arg2) {
        /* do nothing */
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
        return null;
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

    public Serializable getIdentifier(Object arg0, EntityMode arg1) throws HibernateException {
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
        String[] retval = {"ritree.ritree"};
        return retval;
    }

    public Type getPropertyType(String name) throws MappingException {
        return propertyMapping_.toType(name);
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
        return false;
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
        return true;
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

    public Object load(Serializable id, Object optionalObject, LockMode lockModel, SessionImplementor session) throws HibernateException {
        throw new AssertionFailure("Objectd of type 'FunType' must not be directly loaded");
    }

    public void lock(Serializable arg0, Object arg1, Object arg2, LockMode arg3, SessionImplementor arg4) throws HibernateException {
       throw new AssertionFailure("Objectd of type 'FunType' must not be directly loaded");
    }

    public void postInstantiate() throws MappingException {
    
    }

    public void processInsertGeneratedProperties(Serializable arg0, Object arg1, Object[] arg2, SessionImplementor arg3) {
       
    }

    public void processUpdateGeneratedProperties(Serializable arg0, Object arg1, Object[] arg2, SessionImplementor arg3) {
      
    }

    public void resetIdentifier(Object entity, Serializable currentId, Object currentVersion, EntityMode entityMode) {
        getTuplizer(entityMode).resetIdentifier(entity, currentId, currentVersion);
    }

    public void setIdentifier(Object entity, Serializable id, EntityMode entityMode) throws HibernateException {
        getTuplizer(entityMode).setIdentifier(entity, id);
    }

    public void setPropertyValue(Object arg0, int arg1, Object arg2, EntityMode arg3) throws HibernateException {
        throw new AssertionFailure("Object is immutable");
    }

    public void setPropertyValues(Object arg0, Object[] arg1, EntityMode arg2) throws HibernateException {
        throw new AssertionFailure("Object is immutable");
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
        // we does not use filters.
        return "";
    }

    public String fromJoinFragment(String alias, boolean innerJoin, boolean includeSubclasses) {
        // this is 'all otjer' joins.
        return "";
    }

    public String[] getKeyColumnNames() {
        return keyColumnNames_;
    }

    public String getName() {
        return getEntityName();
    }

    public String getTableName() {
        return funName_+"(?,?)";
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
        return ""; //selectFragment(lhsAlias, entitySuffix);
    }


    public String whereJoinFragment(String arg0, boolean arg1, boolean arg2) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    // OuterJoinLoadable




    protected EntityTuplizer getTuplizer(EntityMode entityMode)
    {
        return entityMetamodel_.getTuplizer(entityMode);
    }

    private final String funName_;
    private final ClassMetadata classMetadata_;
    private final EntityMetamodel entityMetamodel_;
    private final SessionFactoryImplementor  factory_;
    private final EntityRegionAccessStrategy cacheAccessStrategy_;
    private final BasicEntityPropertyMapping propertyMapping_;
    private final String[]  keyColumnNames_;

    static final String[] KEY_COLUMN_NAMES =  { "lower", "upper" };

}
