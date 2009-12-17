package ua.gradsoft.hibernateplugin.ritree;

import java.io.Serializable;
import java.util.Map;
import org.hibernate.EntityMode;
import org.hibernate.HibernateException;
import org.hibernate.engine.SessionImplementor;
import org.hibernate.metadata.ClassMetadata;
import org.hibernate.type.EntityType;
import org.hibernate.type.Type;

/**
 *
 * @author rssh
 */
public class RiTreeIntervalClassMetadata implements ClassMetadata
{

    RiTreeIntervalClassMetadata(String entityName, Class userClass)
    {
      entityName_ = entityName;
      entityType_ = new RiTreeIntervalEntityType(userClass);
    }

    public String getEntityName() {
        return entityName_;
    }

    public Serializable getIdentifier(Object entity, EntityMode entityMode) throws HibernateException {
        return ((RiTreeInterval)entity).getPk();
    }

    public String getIdentifierPropertyName() {
        return "pk";
    }

    public Type getIdentifierType() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public Class getMappedClass(EntityMode arg0) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public int[] getNaturalIdentifierProperties() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public boolean[] getPropertyLaziness() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public String[] getPropertyNames() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public boolean[] getPropertyNullability() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public Type getPropertyType(String arg0) throws HibernateException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public Type[] getPropertyTypes() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public Object getPropertyValue(Object arg0, String arg1, EntityMode arg2) throws HibernateException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public Object[] getPropertyValues(Object arg0, EntityMode arg1) throws HibernateException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public Object[] getPropertyValuesToInsert(Object arg0, Map arg1, SessionImplementor arg2) throws HibernateException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public Object getVersion(Object arg0, EntityMode arg1) throws HibernateException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public int getVersionProperty() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public boolean hasIdentifierProperty() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public boolean hasNaturalIdentifier() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public boolean hasProxy() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public boolean hasSubclasses() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public boolean implementsLifecycle(EntityMode arg0) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public boolean implementsValidatable(EntityMode arg0) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public Object instantiate(Serializable arg0, EntityMode arg1) throws HibernateException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public boolean isInherited() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public boolean isMutable() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public boolean isVersioned() {
        throw new UnsupportedOperationException("Not supported yet.");
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
    private Type entityType_;

}
