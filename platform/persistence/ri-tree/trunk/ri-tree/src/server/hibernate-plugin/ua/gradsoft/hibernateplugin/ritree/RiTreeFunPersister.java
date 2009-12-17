package ua.gradsoft.hibernateplugin.ritree;

import java.io.Serializable;
import org.hibernate.MappingException;
import org.hibernate.cache.access.EntityRegionAccessStrategy;
import org.hibernate.engine.SessionFactoryImplementor;
import org.hibernate.mapping.PersistentClass;
import org.hibernate.persister.entity.AbstractEntityPersister;
import org.hibernate.type.Type;



public class RiTreeFunPersister extends AbstractEntityPersister
{

    RiTreeFunPersister(String funName, PersistentClass persistentClass,  EntityRegionAccessStrategy cacheAccessStrategy, SessionFactoryImplementor factory)
    {
      super(persistentClass,cacheAccessStrategy,factory);
      funName_=funName;
      fakeTableName_=funName+"(?,?)";
    }

    @Override
    protected String filterFragment(String arg0) throws MappingException {
        // filter does not touch anything.
        return "";
    }

    @Override
    protected String[] getKeyColumns(int arg0) {
        return keyColumns_;
    }

    @Override
    protected int[] getPropertyTableNumbers() {
        return propertyTableNumbers_;
    }

    @Override
    protected int[] getPropertyTableNumbersInSelect() {
        return propertyTableNumbers_;
    }

    @Override
    protected int[] getSubclassColumnTableNumberClosure() {
        return subclassColumnTableNumberClosure_;
    }

    @Override
    protected int[] getSubclassFormulaTableNumberClosure() {
        return subclassFormulaTableNumberClosure_;
    }

    @Override
    protected int getSubclassPropertyTableNumber(int arg0) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    protected String[] getSubclassTableKeyColumns(int arg0) {
        return EMPTY_STRING_ARRAY;
    }

    @Override
    public String getSubclassTableName(int arg0) {
        throw new UnsupportedOperationException("Not supported: we have no subclass");
    }

    @Override
    protected int getSubclassTableSpan() {
        return 0;
    }

    @Override
    protected String getTableName(int arg0) {
        return fakeTableName_;
    }

    @Override
    protected int getTableSpan() {
        return 0;
    }

    @Override
    protected boolean isClassOrSuperclassTable(int arg0) {
        return arg0==0;
    }

    @Override
    protected boolean isPropertyOfTable(int arg0, int arg1) {
        return arg0==0 && arg1==1;
    }

    @Override
    protected boolean isTableCascadeDeleteEnabled(int arg0) {
        return false;
    }

    public String fromTableFragment(String name) {
        return fakeTableName_+" "+name;
    }

    public String getPropertyTableName(String propertyName)
    {
        return fakeTableName_;
    }

    public String getSubclassPropertyTableName(int arg0) {
        throw new UnsupportedOperationException("Not supported - before has no subclasses");
    }

    public Type getDiscriminatorType() {
        return null;
    }

    public String getSubclassForDiscriminatorValue(Object arg0) {
        throw new UnsupportedOperationException("Not supported: we have no subclasses");
    }

    public Serializable[] getPropertySpaces() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public String getTableName() {
        return fakeTableName_;
    }

    public String[] getConstraintOrderedTableNameClosure() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public String[][] getContraintOrderedTableKeyColumnClosure() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public String getDiscriminatorSQLValue() {
        return null;
    }


    protected String funName_;
    protected String fakeTableName_;
    protected static final String[] keyColumns_ = { "lower","upper" };
    protected static final int[]  propertyTableNumbers_ = { 1, 1 };
    protected static final int[]  subclassColumnTableNumberClosure_ = { 1 };
    protected static final int[]  subclassFormulaTableNumberClosure_ = { };
    protected static final int[]  subclassPropertyTableNumberClosure_ = { };

    protected static final int[]  EMPTY_INT_ARRAY = {};
    protected static final String[]  EMPTY_STRING_ARRAY = {};

}
