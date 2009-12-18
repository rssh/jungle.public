package ua.gradsoft.hibernateplugin.ritree;

import java.io.Serializable;
import java.util.Map;
import org.hibernate.EntityMode;
import org.hibernate.HibernateException;
import org.hibernate.engine.SessionImplementor;
import org.hibernate.metadata.ClassMetadata;
import org.hibernate.persister.entity.EntityPersister;
import org.hibernate.tuple.entity.EntityMetamodel;
import org.hibernate.type.Type;

/**
 *
 * @author rssh
 */
public class RiTreeIntervalClassMetadata implements ClassMetadata
{

    RiTreeIntervalClassMetadata(String entityName, Class userClass, EntityMetamodel entityMetamodel)
    {
      entityName_ = entityName;
      //entityType_ = new RiTreeIntervalEntityType(userClass);
      entityMetamodel_ = entityMetamodel;
    }

    public String getEntityName() {
        return entityName_;
    }

    public Serializable getIdentifier(Object entity, EntityMode entityMode) throws HibernateException {
        return ((RiFakeEntity)entity).getInterval();
    }

    public String getIdentifierPropertyName() {
        return "interval";
    }

    public Type getIdentifierType() {
        return entityMetamodel_.getIdentifierProperty().getType();
    }

    public Class getMappedClass(EntityMode arg0) {
        return entityMetamodel_.getEntityType().getReturnedClass();
    }

    public int[] getNaturalIdentifierProperties() {
        return NATURAL_IDENTIFIER_PROPERTIES;
    }

    public boolean[] getPropertyLaziness() {
        return PROPERTY_LAZINES;
    }

    public String[] getPropertyNames() {
        return PROPERTY_NAMES;
    }

    public boolean[] getPropertyNullability() {
        return PROPERTY_NULLABILITY;
    }

    public Type getPropertyType(String name) throws HibernateException {
        return entityMetamodel_.getPropertyTypes()[entityMetamodel_.getPropertyIndex(name)];
    }

    public Type[] getPropertyTypes() {
        return entityMetamodel_.getPropertyTypes();
    }

    public Object getPropertyValue(Object object, String propertyName, EntityMode entityMode) throws HibernateException {
        if (propertyName.equals("interval")||propertyName.equals(EntityPersister.ENTITY_ID)) {
            return ((RiFakeEntity)object).getInterval();
        }else{
            throw new HibernateException("No such property "+propertyName+" for RiTreeInterval internal class");
        }
    }

    public Object[] getPropertyValues(Object object, EntityMode arg1) throws HibernateException {
        Object[] retval = new Object[3];
        RiFakeEntity e = ((RiFakeEntity)object);
        retval[0]=e.getInterval();
        return retval;
    }

    public Object[] getPropertyValuesToInsert(Object object, Map arg1, SessionImplementor arg2) throws HibernateException {
        Object[] retval = new Object[3];
        RiFakeEntity e = ((RiFakeEntity)object);
        retval[0]=e.getInterval();
        return retval;
    }

    public Object getVersion(Object arg0, EntityMode arg1) throws HibernateException {
        return null;
    }

    public int getVersionProperty() {
        return -1;
    }

    public boolean hasIdentifierProperty() {
        return true;
    }

    public boolean hasNaturalIdentifier() {
        return false;
    }

    public boolean hasProxy() {
        return false;
    }

    public boolean hasSubclasses() {
        return false;
    }

    public boolean implementsLifecycle(EntityMode arg0) {
        return false;
    }

    public boolean implementsValidatable(EntityMode arg0) {
        return false;
    }

    public Object instantiate(Serializable arg0, EntityMode arg1) throws HibernateException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public boolean isInherited() {
        return false;
    }

    public boolean isMutable() {
        return false;
    }

    public boolean isVersioned() {
        return false;
    }

    public void setIdentifier(Object arg0, Serializable arg1, EntityMode arg2) throws HibernateException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void setPropertyValue(Object arg0, String arg1, Object arg2, EntityMode arg3) throws HibernateException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void setPropertyValues(Object arg0, Object[] arg1, EntityMode arg2) throws HibernateException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    private String entityName_;
    private Type   entityType_;
   // private Type   identifierType_;

    private EntityMetamodel entityMetamodel_;
    
    private String[] PROPERTY_NAMES   =              { "interval"};
    private boolean[] PROPERTY_LAZINES=              { false     };
    private int[]    NATURAL_IDENTIFIER_PROPERTIES = { 0  };
    private boolean[] PROPERTY_NULLABILITY =         { false     };

}
