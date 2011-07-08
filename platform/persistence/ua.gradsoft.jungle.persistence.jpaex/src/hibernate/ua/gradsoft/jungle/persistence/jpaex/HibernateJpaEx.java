package ua.gradsoft.jungle.persistence.jpaex;

import javax.persistence.EntityManager;
import org.hibernate.Filter;
import org.hibernate.Session;
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



    @Override
    public JdbcEx  getJdbcEx()
    { return jdbcEx_; }

    public void setJdbcEx(JdbcEx jdbcEx)
    { jdbcEx_=jdbcEx; }
    
    public boolean getCloseConnection()
    { return aggressiveClose_; }

    public void setCloseConnection(boolean closeConnection)
    { aggressiveClose_=closeConnection; }

    @Override
    public boolean isFiltersSupported() {
        return true;
    }


    @Override
    public boolean isFilterEnabled(EntityManager entityManager, String filterName) {
        Session session = (Session)entityManager.getDelegate();
        return session.getEnabledFilter(filterName)!=null;
    }


    @Override
    public void setFilterEnabled(EntityManager entityManager, String filterName, boolean enabled) {
        Session session = (Session)entityManager.getDelegate();
        if (enabled) {
          session.enableFilter(filterName);
        }else{
          session.disableFilter(filterName);
        }
    }

    @Override
    public void setFilterParameter(EntityManager entityManager, String filterName, String paramName, Object param) {
       Session session = (Session)entityManager.getDelegate();
       Filter filter = session.getEnabledFilter(filterName);
       if (filter==null) {
           // enable if was disabled.
           session.enableFilter(filterName);
           filter = session.getEnabledFilter(filterName);
           if (filter==null) {
               throw new IllegalArgumentException("No such filter "+filterName);
           }
       }
       filter.setParameter(paramName, param);
    }



    private boolean aggressiveClose_;
    private JdbcEx  jdbcEx_;

}
