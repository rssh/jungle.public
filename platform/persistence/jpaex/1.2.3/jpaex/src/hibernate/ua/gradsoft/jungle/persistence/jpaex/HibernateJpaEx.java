package ua.gradsoft.jungle.persistence.jpaex;

import javax.persistence.EntityManager;
import org.hibernate.ejb.HibernateEntityManager;
import ua.gradsoft.jungle.persistence.jdbcex.JdbcEx;


public class HibernateJpaEx extends JpaEx
{

    @Override
    public String getDialect() {
        return "hibernate";
    }

    @Override
    public boolean isJdbcConnectionWrapperSupported(EntityManager em) {
        return true;
    }

    @Override
    public JdbcConnectionWrapper getJdbcConnectionWrapper(EntityManager em, boolean readOnly) {
        if (em instanceof HibernateEntityManager) {
            HibernateEntityManager hem = (HibernateEntityManager)em;
            return new HibernateJdbcConnectionWrapper(hem.getSession().connection(),aggressiveClose_);
        }else{
            throw new IllegalStateException("entity manager is not HibernateEntityManager");
        }
    }

    public JdbcEx  getJdbcEx()
    { return jdbcEx_; }

    public void setJdbcEx(JdbcEx jdbcEx)
    { jdbcEx_=jdbcEx; }
    

    public boolean getCloseConnection()
    { return aggressiveClose_; }

    public void setCloseConnection(boolean closeConnection)
    { aggressiveClose_=closeConnection; }

    private boolean aggressiveClose_;
    private JdbcEx  jdbcEx_;

}
