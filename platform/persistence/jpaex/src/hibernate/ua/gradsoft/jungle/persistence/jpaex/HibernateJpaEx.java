package ua.gradsoft.jungle.persistence.jpaex;

import javax.persistence.EntityManager;
import org.hibernate.ejb.HibernateEntityManager;


public class HibernateJpaEx extends JpaEx
{

    @Override
    public JdbcConnectionWrapper getJdbcConnectionWrapper(EntityManager em, boolean readOnly) {
        if (em instanceof HibernateEntityManager) {
            HibernateEntityManager hem = (HibernateEntityManager)em;
            return new HibernateJdbcConnectionWrapper(hem.getSession().connection(),aggressiveClose_);
        }else{
            throw new IllegalStateException("entity manager is not HibernateEntityManager");
        }
    }

    public boolean getCloseConnection()
    { return aggressiveClose_; }

    public void setCloseConnection(boolean closeConnection)
    { aggressiveClose_=closeConnection; }

    private boolean aggressiveClose_;

}
