package ua.gradsoft.hibernateplugin.ritree;

import org.hibernate.cache.access.EntityRegionAccessStrategy;
import org.hibernate.engine.Mapping;
import org.hibernate.engine.SessionFactoryImplementor;
import org.hibernate.mapping.PersistentClass;

/**
 *'Persister for  'Contains or Equals'
 */
public class RiContainsEqPersister extends RiTreeFunPersister
{

   public RiContainsEqPersister(PersistentClass persistentClass,
                            EntityRegionAccessStrategy cacheAccessStrategy,
                            SessionFactoryImplementor factory,
                            Mapping mapping)
   { super("ri_tree.ri_time_intervals_contains_eq",persistentClass,cacheAccessStrategy,factory);
   }


}
