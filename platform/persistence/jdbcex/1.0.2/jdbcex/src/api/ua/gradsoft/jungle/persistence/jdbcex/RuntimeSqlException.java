package ua.gradsoft.jungle.persistence.jdbcex;

import java.sql.SQLException;

/**
 *SQLException, wrapped in RuntimeException
 * @author rssh
 */
public class RuntimeSqlException extends RuntimeException
{

    /**
     * create sql exception.
     * @param ex
     */
    public RuntimeSqlException(SQLException ex)
    {
      super(ex.getMessage(),ex);  
    }

    /**
     * @return wrapped SQL Exception.
     */
    public SQLException getSQLException()
    {
      return (SQLException)getCause();
    }

    /**
     * exception vendor-specific sql error code.
     */
    public int getVendorErrorCode()
    {
      return getSQLException().getErrorCode();
    }

    public String getSQLState()
    {
      return getSQLException().getSQLState();
    }

}
