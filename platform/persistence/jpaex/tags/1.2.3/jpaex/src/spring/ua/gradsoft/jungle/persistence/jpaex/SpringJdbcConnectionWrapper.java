
package ua.gradsoft.jungle.persistence.jpaex;

import java.sql.Connection;
import org.springframework.jdbc.datasource.ConnectionHandle;

/**
 *Jdbc connection handle for spring.
 * (wrapper arround spring ConnectionHandle)
 * @author rssh
 */
class SpringJdbcConnectionWrapper implements JdbcConnectionWrapper
{

    SpringJdbcConnectionWrapper(ConnectionHandle connectionHandle)
    { connectionHandle_=connectionHandle; }

    public Connection getConnection()
    { return connectionHandle_.getConnection(); }

    public void releaseConnection(Connection connection)
    {  connectionHandle_.releaseConnection(connection); }

    private ConnectionHandle connectionHandle_;
}
