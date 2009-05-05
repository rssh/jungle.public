
package ua.gradsoft.jungle.localization;

import java.io.Serializable;

/**
 *Primary Key for associations between translation table and column
 * @author rssh
 */
public class PkTranslationTableColumn implements Serializable
{

    public String getTableName()
    {
      return tableName_;  
    }

    public void setTableName(String tableName)
    {
      tableName_=tableName;
    }

    public String getColumnName()
    {
      return columnName_;
    }

    public void setColumnName(String columnName)
    {
      columnName_=columnName;
    }

    @Override
    public boolean equals(Object o)
    {
        if (o instanceof PkTranslationTableColumn) {
            PkTranslationTableColumn pko = (PkTranslationTableColumn)o;
            if (getTableName()==null) {
                if (pko.getTableName()==null) {
                    return getColumnName().equals(pko.getColumnName());
                }else{
                    return false;
                }
            }else if (getTableName().equals(pko.getTableName())) {
                return getColumnName().equals(pko.getColumnName());
            }else{
                return false;
            }
        }else{
            return false;
        }
    }

    @Override
    public int hashCode()
    {
      return (((tableName_==null) ? 0 : tableName_.hashCode()) +
              ((columnName_==null) ? 0 : columnName_.hashCode()));
    }

    private String tableName_;
    private String columnName_;

}
