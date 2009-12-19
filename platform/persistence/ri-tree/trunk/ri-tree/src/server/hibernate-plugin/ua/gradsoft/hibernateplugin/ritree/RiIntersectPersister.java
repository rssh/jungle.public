package ua.gradsoft.hibernateplugin.ritree;

import org.hibernate.cache.access.EntityRegionAccessStrategy;
import org.hibernate.engine.Mapping;
import org.hibernate.engine.SessionFactoryImplementor;
import org.hibernate.mapping.PersistentClass;

/**
 *Persister for 'Intersection'
 */
public class RiIntersectPersister extends RiTreeFunPersister
{

    public RiIntersectPersister(PersistentClass persistentClass,
                            EntityRegionAccessStrategy cacheAccessStrategy,
                            SessionFactoryImplementor factory,
                            Mapping mapping)
   { super("ri_tree.ri_time_intervals_during_eq",persistentClass,cacheAccessStrategy,factory);
   }



}
