package ua.gradsoft.jungle.persistence.ritree;

import javax.persistence.EntityManager;

/**
 *Interface for operations with RiIntervals
 * @author rssh
 */
public interface IRiIntervals {

    void persist(EntityManager em, RiInterval ri);

    void remove(EntityManager em, RiInterval ri);

    boolean exists(EntityManager em, RiInterval ri);

}
