
package ua.gradsoft.jungle.localization;

import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.Table;

/**
 *Description for one column in bundle
 * @author rssh
 */
@Entity
@Table(name="localization_bundle_table_columns")
@IdClass(PkTranslationTableColumn.class)
public class TranslationTableColumn implements Serializable
{

    @Id
    @Column(name="table_name")
    public String getTableName()
    {
      return tableName_;
    }

    public void setTableName(String tableName)
    {
      tableName_=tableName;
    }

    @Id
    @Column(name="column_prefix")
    public String getColumnName()
    {
      return columnName_;
    }

    public void setColumnName(String columnName)
    {
      columnName_=columnName;
    }


    private String tableName_;
    private String columnName_;


}
