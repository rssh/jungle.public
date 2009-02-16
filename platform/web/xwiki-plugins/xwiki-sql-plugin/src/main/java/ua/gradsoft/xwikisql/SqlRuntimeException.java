/*
 */

package ua.gradsoft.xwikisql;

import java.sql.SQLException;

/**
 *Runtime exception, which denote
 *errors inside SQL.
 * 
 * @see SqlPlugin#handleError
 * @see SqlPluginConfiguration getThrowExceptions
 * @author rssh
 */
public class SqlRuntimeException extends RuntimeException
{

    public SqlRuntimeException(String message)
    { super(message); }
    
    public SqlRuntimeException(String message, Exception ex)
    { super(message, ex); }

    public SqlRuntimeException(Exception ex)
    { super(ex.getMessage(), ex); }
    
    
}
