
package ua.gradsoft.hibernateplugin.ritree;

import javax.persistence.EntityManager;
import ua.gradsoft.jungle.persistence.ritree.IRiIntervals;
import ua.gradsoft.jungle.persistence.ritree.RiInterval;

/**
 *
 * @author rssh
 */
public class HibernateRiTreeIntervals implements IRiIntervals
{

    public boolean exists(EntityManager em, RiInterval ri) {
        RiTreeInterval rti = em.find(RiTreeInterval.class, ri);
        return (rti!=null);
    }

    public void persist(EntityManager em, RiInterval ri) {
        RiTreeInterval rti = new RiTreeInterval();
        rti.setPk(ri);
        em.persist(rti);
    }

    public void remove(EntityManager em, RiInterval ri) {
        RiTreeInterval rti = new RiTreeInterval();
        rti.setPk(ri);
        em.remove(rti);
    }

}
