package ua.gradsoft.jungle.persistence.jpaex;

import java.sql.SQLException;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceException;
import org.springframework.orm.jpa.JpaDialect;
import ua.gradsoft.jungle.persistence.jdbcex.JdbcEx;


public class SpringJpaEx extends JpaEx
{

    @Override
    public String getDialect() {
        return getJpaDialect().toString();
    }

    @Override
    public boolean isJdbcConnectionWrapperSupported(EntityManager em) {
        return true;
    }

   public JdbcConnectionWrapper getJdbcConnectionWrapper(EntityManager em, boolean readOnly)
   {
     try {
       return new SpringJdbcConnectionWrapper(jpaDialect_.getJdbcConnection(em,readOnly));
     }catch(SQLException ex){
         throw new PersistenceException("exception during creating jdbcWrapper",ex);
     }
   }


    /**
     * @return false
     */
    @Override
    public boolean isFiltersSupported() {
        return false;
    }

    /**
     * throws UnsupportedOperationException
     */
    @Override
    public boolean isFilterEnabled(EntityManager entityManager, String filterName) {
        throw new UnsupportedOperationException("Not supported.");
    }

    /**
     * throws UnsupportedOperationException
     */
    @Override
    public void setFilterEnabled(EntityManager entityManager, String arg0, boolean arg1) {
        throw new UnsupportedOperationException("Not supported.");
    }

    @Override
    public void setFilterParameter(EntityManager entityManager, String arg0, String arg1, Object arg2) {
        throw new UnsupportedOperationException("Not supported.");
    }



   /**
    * JdbcEx instance must be configured in IOP as singleton
    * @return jdbcex
    */
   public JdbcEx  getJdbcEx()
   {
       return JdbcEx.getInstance();
   }

   public JpaDialect getJpaDialect()
   { return jpaDialect_; }

   public void setJpaDialect(JpaDialect jpaDialect)
   { jpaDialect_=jpaDialect; }

   private JpaDialect jpaDialect_;
}
