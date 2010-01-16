package ua.gradsoft.hibernateplugin.ritree;

import java.io.Serializable;
import java.util.Map;
import org.hibernate.EntityMode;
import org.hibernate.HibernateException;
import org.hibernate.engine.SessionImplementor;
import org.hibernate.metadata.ClassMetadata;
import org.hibernate.persister.entity.EntityPersister;
import org.hibernate.tuple.entity.EntityMetamodel;
import org.hibernate.type.LongType;
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
        if (isId(name)) {
            return entityMetamodel_.getIdentifierProperty().getType();
        }else if (name.equals("interval.begin")||name.equals("interval.end")||
                  name.equals("id.begin")||name.equals("id.end")) {
            return LONG_TYPE;
        }else{
            return entityMetamodel_.getPropertyTypes()[entityMetamodel_.getPropertyIndex(name)];
        }
    }

    public Type[] getPropertyTypes() {
        Type[] retval = new Type[3];
        retval[0]=entityMetamodel_.getIdentifierProperty().getType();
        retval[1]=LONG_TYPE;
        retval[2]=LONG_TYPE;
        return retval;
    }

    public Object getPropertyValue(Object object, String propertyName, EntityMode entityMode) throws HibernateException {
        if (propertyName.equals("interval")||propertyName.equals(EntityPersister.ENTITY_ID)) {
            return ((RiFakeEntity)object).getInterval();
        }else if (propertyName.equals("interval.begin")||propertyName.equals("id.begin")) {
            return ((RiFakeEntity)object).getInterval().getBegin();
        }else if (propertyName.equals("interval.end")||propertyName.equals("id.end")) {
            return ((RiFakeEntity)object).getInterval().getEnd();
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

    private boolean isId(String propertyName)
    {
      return propertyName.equals("interval")||propertyName.equals(EntityPersister.ENTITY_ID);  
    }

    private String entityName_;
   // private Type   entityType_;
   // private Type   identifierType_;

    private EntityMetamodel entityMetamodel_;
    
    private static final String[] PROPERTY_NAMES   =              { "interval", "interval.begin", "interval.end" };
    private static final boolean[] PROPERTY_LAZINES=              { false     ,  false, false };
    private static final int[]    NATURAL_IDENTIFIER_PROPERTIES = { 0  ,  0, 0 };
    private static final boolean[] PROPERTY_NULLABILITY =         { false, false, false };

    private static final Type LONG_TYPE = new LongType();

}
