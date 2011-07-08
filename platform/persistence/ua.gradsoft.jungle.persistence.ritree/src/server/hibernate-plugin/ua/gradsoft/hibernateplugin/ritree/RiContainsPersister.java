package ua.gradsoft.hibernateplugin.ritree;

import org.hibernate.cache.access.EntityRegionAccessStrategy;
import org.hibernate.engine.Mapping;
import org.hibernate.engine.SessionFactoryImplementor;
import org.hibernate.mapping.PersistentClass;

/**
 *Persister for RiContains
 * @author rssh
 */
public class RiContainsPersister extends RiTreeFunPersister
{

   public RiContainsPersister(PersistentClass persistentClass,
                            EntityRegionAccessStrategy cacheAccessStrategy,
                            SessionFactoryImplementor factory,
                            Mapping mapping)
   { super("ri_tree.ri_time_intervals_contains",persistentClass,cacheAccessStrategy,factory);
   }


}
