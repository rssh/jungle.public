package ua.gradsoft.xwikisql;

import java.util.Enumeration;

/**
 * Assess object for results of query execution.
 * Object can be
 * <ul>
 *  <li> 
 *   result set, where rowps are avaible throught enumeration interface. 
 * </li>
 * <li> 
 *   track record about exception during creation of query. (when wasError 
 *   is true)
 * </li>
 * </ul>
 **/
public interface SqlResult extends Enumeration<SqlResultRow>, SqlErrorable
{

    /**         
     * get number of columns
     * @return number of colums.
     */
   int getNColumns();
   
   /**
    * get number of rows. 
    * @return number of rows.
    */
   int getNRows();
   
   /**
    * Indicate situation, where we does not retrieve
    * all rows. (for example, when select statement
    * return us more than configurated maximumRows)
    * 
    * @return true, if not all rows retrieced 
    * into sql result, otherwise false.
    */
   boolean  isNotAllRows();
   
   /**   
    * get column name if one exists, otherwise handle error.
    * @param index (starting from 0)
    * @return name of appropriative column.
    */
   String   getColumnName(int index);
   
   /**       
    * check if <code> name </code> is one of column names.
    * @param name to check.
    * @return true if <code> name </code> is really a column name.
    */
   boolean  hasColumnName(String name);
   
   /**
    * retrieve column index by name;
    * 
    * @return columnIndex (started from 0) 
    *         or -1 if such name is not exists.
    */
   int getColumnIndex(String name);
  
   
   /**         
    * retrieve int value of column type by name,
    * as specified in java.sql.Types
    * @param name - colum name.
    * @return type of colum as int (or null)
    * @see java.sql.Types
    */
   int getColumnTypeAsInt(String name);
   
   /**         
    * retrieve int value of column type by index,
    * @param index - index of column (starting from 0)
    * @return type of colum as int (or null)
    */
   int getColumnTypeAsInt(int index);
   
  
   /**         
    * retrieve name of column type by column name,
    * as specified in java.sql.Types
    * @param name - colum name.
    * @return typename
    * @see java.sql.Types
    */
   String getColumnType(String name);
   
   /**         
    * retrieve name of column type by column index,
    * @param index - index of column (starting from 0)
    * @return column type  as string.
    * @see java.sql.Types
    */
   String getColumnType(int  index);
    
}
