package ua.gradsoft.jungle.persistence.jpaex;

import java.sql.SQLException;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceException;
import javax.sql.DataSource;
import ua.gradsoft.jungle.persistence.jdbcex.JdbcEx;

/**
 *JpaEx used in full J2EE ejb environments.
 * (when we create jdbc datasource inside JTA transaction)
 * @author rssh
 */
public class JtaWithKnownDataSourceJpaEx extends JpaEx
{

    /**
     * @return "jtads"
     */
    @Override
    public String getDialect() {
        return "jtads";
    }

    @Override
    public boolean isJdbcConnectionWrapperSupported(EntityManager em) {
        return true;
    }

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

    public JdbcEx getJdbcEx()
    { return jdbcEx_; }



    /**
     * JdbcEx is set during creation.
     * @param jdbcEx
     */
    public void setJdbcEx(JdbcEx jdbcEx)
    {
      jdbcEx_=jdbcEx;  
    }

    /**
     * @return false
     */
   @Override
    public boolean isFiltersSupported() {
        return false;
    }

    
    /**
     * throw UnsupportedOperationException
     */
    @Override
    public boolean isFilterEnabled(EntityManager entityManager, String filterName) {
        throw new UnsupportedOperationException("Not supported.");
    }

    /**
     * throw UnsupportedOperationException
     */
    @Override
    public void setFilterEnabled(EntityManager entityManager, String filterName, boolean enabled) {
        throw new UnsupportedOperationException("Not supported.");
    }

   /**
     * throw UnsupportedOperationException
     */
    @Override
    public void setFilterParameter(EntityManager entityManager, String filterName, String paramName, Object param) {
        throw new UnsupportedOperationException("Not supported.");
    }




    private DataSource ds_;
    private JdbcEx     jdbcEx_;
}
