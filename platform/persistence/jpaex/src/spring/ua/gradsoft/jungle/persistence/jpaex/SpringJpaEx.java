package ua.gradsoft.jungle.persistence.jpaex;

import java.sql.SQLException;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceException;
import org.springframework.orm.jpa.JpaDialect;


public class SpringJpaEx extends JpaEx
{

   public JdbcConnectionWrapper getJdbcConnectionWrapper(EntityManager em, boolean readOnly)
   {
     try {
       return new SpringJdbcConnectionWrapper(jpaDialect_.getJdbcConnection(em,readOnly));
     }catch(SQLException ex){
         throw new PersistenceException("exception during creating jdbcWrapper",ex);
     }
   }

   public JpaDialect getJpaDialect()
   { return jpaDialect_; }

   public void setJpaDialect(JpaDialect jpaDialect)
   { jpaDialect_=jpaDialect; }

   private JpaDialect jpaDialect_;
}
