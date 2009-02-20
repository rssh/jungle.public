package ua.gradsoft.jungle.persistence.jpaex;

import java.sql.Connection;

/**
 * Wrapper for Jdbc connection.
 *@see ua.gradsoft.jungle.persistence.jpaex; 
 **/ 
public interface JdbcConnectionWrapper
{

  /**
   * get JDBC connection, which is situated in current transaction.
   **/
  Connection  getConnection();

  /**
   * release JDBC connection - user must use this method instead call of
   * Connection.close()
   **/
  void        releaseConnection(Connection connection);

}
