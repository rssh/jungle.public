
package ua.gradsoft.jungle.localization;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 *Copy of translation table, which does not have
 * internal hibernate proxies.
 *
 * @author rssh
 */
class TranslationTableLocal implements Serializable
{

    public String getTableName()
    { return tableName_; }

    public void setTableName(String tableName)
    { tableName_=tableName; }

    public String getTranslationTableName()
    {
      return translationTableName_;
    }

    public void setTranslationTableName(String translationTableName)
    {
        translationTableName_=translationTableName;
    }

    public String getPkColumnName()
    { return pkColumnName_; }

    public void setPkColumnName(String pkColumnName)
    { pkColumnName_=pkColumnName; }

    public String getEntityClassName()
    { return entityClassName_; }

    public void setEntityClassName(String entityClassName)
    {
        entityClassName_=entityClassName;
    }

    public String  getBundleName()
    { return bundleName_; }

    public void  setBundleName(String bundleName)
    { bundleName_=bundleName; }

    public List<TranslationTableColumn>  getTranslatedColumns()
    { return translatedColumns_; }

    public void setTranslatedColumns(List<TranslationTableColumn> translatedColumns)
    {
      translatedColumns_=translatedColumns;
    }

    /**
     * do deep copy of translation table, to eliminate hibernate
     * proxies.
     * @param tt
     */
    public TranslationTableLocal(TranslationTable tt)
    {
      tableName_ = tt.getTableName();
      translationTableName_ = tt.getTranslationTableName();
      pkColumnName_ = tt.getPkColumnName();
      entityClassName_ = tt.getEntityClassName();
      bundleName_ = tt.getBundle().getName();
      translatedColumns_ = new ArrayList<TranslationTableColumn>(tt.getTranslatedColumns().size());
      for(TranslationTableColumn ttc: tt.getTranslatedColumns())
      {
        translatedColumns_.add(ttc);
      }
    }

    private String tableName_;
    private String translationTableName_;
    private String pkColumnName_;
    private String entityClassName_;
    private String bundleName_;
    private List<TranslationTableColumn>  translatedColumns_;


}
