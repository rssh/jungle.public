package ua.gradsoft.jungle.persistence.jpaex;

import java.sql.Connection;
import java.sql.SQLException;
import javax.sql.DataSource;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;



/**
 *Connection wrapper for JTA/DS
 * @author rssh
 */
public class JtaDsJdbcConnectionWrapper implements JdbcConnectionWrapper
{


    public JtaDsJdbcConnectionWrapper(DataSource ds) throws SQLException
    {
        cn_=ds.getConnection();
    }

    public Connection getConnection() {
        return cn_;
    }

    public void releaseConnection(Connection connection) {
        try {
          cn_.close();
        }catch(SQLException ex){
            Log log = LogFactory.getLog(JtaDsJdbcConnectionWrapper.class);
            log.warn("exception during closing of connection",ex);
        }
    }


    private Connection cn_;
}
