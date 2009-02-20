package ua.gradsoft.jungle.persistence.jpaex;

import java.sql.SQLException;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceException;
import javax.sql.DataSource;

/**
 *JpaEx used in full J2EE ejb environments.
 * (when we create jdbc datasource inside JTA transaction)
 * @author rssh
 */
public class JtaWithKnownDataSourceJpaEx extends JpaEx
{

    @Override
    public JdbcConnectionWrapper getJdbcConnectionWrapper(EntityManager em, boolean readOnly) {
      try{
        return new JtaDsJdbcConnectionWrapper(ds_);
      }catch(SQLException ex){
          throw new PersistenceException("Can't open Jdbc connection", ex);
      }
    }

    public DataSource getDataSource()
    { return ds_; }

    public void setDataSource(DataSource ds)
    { ds_=ds; }

    private DataSource ds_;
}
