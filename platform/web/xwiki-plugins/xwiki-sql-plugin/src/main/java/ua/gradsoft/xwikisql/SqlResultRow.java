
package ua.gradsoft.xwikisql;

import java.math.BigDecimal;
import java.util.Date;


/**
 * Temporary object, which used for navigation accross query results
 * in velocity #foreach loops.
 * i. e.
 * <pre>
 *  #set($resultset=sql.query("db","select * from table"))
 *  #foreach($row in $resultset)
 *  &lt;tr&gt;
 *    &lt;td&gt;$row.getString("column1")&lt;/td&gt;
 *    &lt;td&gt;$row.getString("column2")&lt;/td&gt;
 *  &lt;tr&gt;
 *  #end
 * </pre>
 *
 */
public interface SqlResultRow {

    
    
    /**         
     * retrieve object in column with name <code> name </code>
     * @param name - column name.
     * @return object which situated on this column name.
     */
    Object getObject(String name);
    
    /**         
     * retrieve object in column with index <code> i </code>
     * @param i -- column index (starting from 0)
     * @return object which situated on this column index.
     */    
    Object getObject(int i);
 
    /**
     * true is object in appropriative column is null.
     **/
    boolean isNull(String name);
    
    /**
     * true is object in appropriative column is null.
     *@param i column index (starting from 0)
     **/
    boolean isNull(int i);
    
    /**
     * retrieve object in column with name <code> name </code> as string.
     *@see ua.gradsoft.xwikisql.SqlPluginConfiguration#getNullAsEmptyString() 
     **/
    String  getString(String name);

    /**
     * retrieve object in column with index <code> i </code> as string.
     * @see ua.gradsoft.xwikisql.SqlPluginConfiguration#getNullAsEmptyString() 
     **/    
    String  getString(int i);
    
    /**
     * retrieve object in column with name <code> name </code> as long.
     *If oject is not convertable to long than throw SqlRuntimeException or
     *log error and return null, in depend from throwExceptions configuration
     *parameter.
     **/
    Long    getLong(String name);

    /**
     * retrieve object in column with index <code> i </code> as long.
     * If oject is not convertable to long then behaviour depends from
     * throwExceptions configuration parameter.
     **/
    Long    getLong(int i);

    /**
     * retrieve object in column with name <code> name </code> as integer.
     *If oject is not convertable to Integer: throw SqlRuntimeException or
     *log error and return null, in depend from throwExceptions configuration
     *paraeter.
     **/
    Integer    getInteger(String name);

    /**
     * retrieve object in column with index <code> i </code> as long.
     * If oject is not convertable to Integer then behaviour depends from
     * throwExceptions configuration parameter.
     **/
     Integer    getInteger(int i);
    
    

    /**
     * retrieve object in column with name <code> name </code> as BigDecimal.
     * If oject is not convertable to BigDecimal then behaviour depends from
     * throwExceptions configuration parameter.
     **/
    BigDecimal  getBigDecimal(String name);

    /**
     * retrieve object in column with index <code> i </code> as BigDecimal.
     * If oject is not convertable to long then behaviour depends from
     * throwExceptions configuration parameter.
     **/
    BigDecimal  getBigDecimal(int i);
    
    /**
     * retrieve object in column with name <code> name </code> as Double.
     * If oject is not convertable to double then behaviour depends from
     * throwExceptions configuration parameter.
     **/
    Double      getDouble(String name);
    
    /**
     * retrieve object in column with index <code> i </code> as Double.
     * If oject is not convertable to double then behaviour depends from
     * throwExceptions configuration parameter.
     **/
    Double      getDouble(int i);
        
    /**
     * retrieve object in column with name <code> name </code> as Boolean.
     * If oject is not convertable to boolean then behaviour depends from
     * throwExceptions configuration parameter.
     **/
    Boolean     getBoolean(String name);
    
    /**
     * retrieve object in column with index <code> i </code> as Boolean.
     * If oject is not convertable to boolean then behaviour depends from
     * throwExceptions configuration parameter.
     *@param i index, staring from 0
     **/
    Boolean     getBoolean(int i);
        
    /**
     * retrieve object in column with name <code> name </code> as Date.
     * If oject is not convertable to date then behaviour depends from
     * throwExceptions configuration parameter.
     *param name name of column to retrieve.
     **/
    Date        getDate(String name);
    
    /**
     * retrieve object in column with index <code> i </code> as Date.
     * If oject is not convertable to date then behaviour depends from
     * throwExceptions configuration parameter.
     *@param i column index, staring from 0
     **/
    Date        getDate(int i);
}


