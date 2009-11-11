/*
 */

package ua.gradsoft.xwikisql;


/**
 *Generic interface for objects, which can track sql errors.
 */
public interface SqlErrorable {
    
    
    /**  
     * also set own last exception and wasError to appropriative values.
     * If configuartion parameter throwException is set, throw received exception
     * @param message - message to log (or throw). Can be null if nothing specifcs.
     * @param sql - sql which cause this error. Can be null if unavaible.
     * @param cause - previous exception in chain, if exists.
     * @return exception, which consists from message and cause.
     */
    Exception handleError(String message, String sql, Exception cause);
    
    /**           
     * true if error was handled (and later not cleared) during
     * some previous operations on this object 
     * @return true if one of previous actions cause sql error.
     */
    boolean wasError();
    
    /**
     * get last Exception     
     * @return  last exception if wasError, otherwise null.
     */
    Exception getLastException();

    /**
     * set last Exception     
     */
    void setLastException(Exception ex);
    
    
    /**     
     * clear all information about error, so wasError become false.
     */
    void clearError();
            
    /**
     * when this property is set to true, than engine throws exception on error,
     * otherwise - method usially return null and exceptions are saved for 
     * further retrieving.
     * @see ua.gradsoft.xwikisql.SqlPluginConfiguration#getThrowExceptions  
     * @see ua.gradsoft.xwikisql.SqlErrorable#setThrowExceptions  
     */
    boolean getThrowExceptions();
    
    
    /**
     * set throwExceptions property,    
     * @param value -- value to set
     * @see ua.gradsoft.xwikisql.SqlErrorable#getThrowExceptions       
     */
    void setThrowExceptions(boolean value);
    
    /**
     * when this propery is set to true  - 
     * do logging according with settings in xwiki configurations, 
     * otherwise - complete disable one.
     * Initial value is true (by default) and set from value of parent object.
     * (and for top-level objects can be set via configuration.)
     *@see ua.gradsoft.xwikisql.SqlPluginConfiguration#getLogEnabled   
     */ 
    boolean getLogEnabled();

    /**
     * set logEnabled property,    
     * @param value -- value to set
     * @see ua.gradsoft.xwikisql.SqlErrorable#getLogEnabled      
     */
    void setLogEnabled(boolean value);
    
    
}
