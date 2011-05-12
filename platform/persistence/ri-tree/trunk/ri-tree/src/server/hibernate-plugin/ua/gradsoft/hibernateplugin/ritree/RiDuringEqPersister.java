    package ua.gradsoft.hibernateplugin.ritree;

import org.hibernate.cache.access.EntityRegionAccessStrategy;
import org.hibernate.engine.Mapping;
import org.hibernate.engine.SessionFactoryImplementor;
import org.hibernate.mapping.PersistentClass;

/**
 *Persister for during or eq
 * @author rssh
 */
public class RiDuringEqPersister extends RiTreeFunPersister
{

    public RiDuringEqPersister(PersistentClass persistentClass,
                            EntityRegionAccessStrategy cacheAccessStrategy,
                            SessionFactoryImplementor factory,
                            Mapping mapping)
   { super("ri_tree.ri_time_intervals_during_eq",persistentClass,cacheAccessStrategy,factory);
   }




}
