package ua.gradsoft.jungle.persistence.jdbcex;

import java.sql.SQLException;

/**
 *SQLException, wrapped in RuntimeException
 * @author rssh
 */
public class RuntimeSqlException extends RuntimeException
{

    public RuntimeSqlException(SQLException ex)
    {
      super(ex.getMessage(),ex);  
    }

}
