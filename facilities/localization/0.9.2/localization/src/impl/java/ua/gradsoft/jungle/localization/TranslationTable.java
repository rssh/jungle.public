package ua.gradsoft.jungle.localization;

import java.util.List;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

/**
 * part of bundle
 **/
@Entity
@Table(name="localization_bundle_tables")
public class TranslationTable
{

    @Id
    @Column(name="table_name", length=TABLE_NAME_LENGTH)
    public String getTableName()
    { return tableName_; }

    public void setTableName(String tableName)
    { tableName_=tableName; }

    @Column(name="translation_table_name", length=TABLE_NAME_LENGTH, unique=true)
    public String getTranslationTableName()
    {
      return translationTableName_;
    }

    public void setTranslationTableName(String translationTableName)
    {
        translationTableName_=translationTableName;
    }

    @Column(name="pk_column_name", length=PK_COLUMN_NAME_LENGTH)
    public String getPkColumnName()
    { return pkColumnName_; }

    public void setPkColumnName(String pkColumnName)
    { pkColumnName_=pkColumnName; }

    @Column(name="entity_classname", length=ENTITY_CLASSNAME_LENGTH, unique=true)
    public String getEntityClassName()
    { return entityClassName_; }

    public void setEntityClassName(String entityClassName)
    {
        entityClassName_=entityClassName;
    }

    /**     
     * @return owner for this bundle.
     */
    @ManyToOne
    @JoinColumn(name="bundle_name", referencedColumnName="name")
    public BundleInfo  getBundle()
    { return bundle_; }

    public void  setBundle(BundleInfo bundle)
    { bundle_=bundle; }

    @OneToMany
    @JoinColumn(name="table_name")
    public List<TranslationTableColumn>  getTranslatedColumns()
    { return translatedColumns_; }
    
    public void setTranslatedColumns(List<TranslationTableColumn> translatedColumns)
    {
      translatedColumns_=translatedColumns;  
    }

    private String tableName_;
    private String translationTableName_;
    private String pkColumnName_;
    private String entityClassName_;
    private BundleInfo  bundle_;
    private List<TranslationTableColumn>  translatedColumns_;

    public static final int TABLE_NAME_LENGTH = 64;
    public static final int PK_COLUMN_NAME_LENGTH = 32;
    public static final int ENTITY_CLASSNAME_LENGTH = 255;


}
