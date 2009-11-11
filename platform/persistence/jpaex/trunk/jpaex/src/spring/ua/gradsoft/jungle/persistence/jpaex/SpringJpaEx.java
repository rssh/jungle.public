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
    * JdbcEx instance must be configured in IOP as singleton
    * @return
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
