
package ua.gradsoft.jungle.persistence.jpaex;

import java.sql.Connection;
import java.sql.SQLException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 *JDBC Connection wrapper for hibernate
 * @author rssh
 */
class HibernateJdbcConnectionWrapper implements JdbcConnectionWrapper
{

    HibernateJdbcConnectionWrapper(Connection connection, boolean toClose)
    {
      connection_=connection;  
      toClose_=toClose;
    }

    public Connection getConnection() {
        return connection_;
    }

    public void releaseConnection(Connection connection) {
        if (toClose_) {
          try {
            connection_.close();
          }catch(SQLException ex){
             Log log = LogFactory.getLog(HibernateJdbcConnectionWrapper.class);
             log.warn("exception during closing of hibernat connection");
          }
        }
    }


    private Connection connection_;
    boolean toClose_;

}
