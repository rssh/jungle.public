
package ua.gradsoft.hibernateplugin.ritree;

import org.hibernate.cache.access.EntityRegionAccessStrategy;
import org.hibernate.engine.Mapping;
import org.hibernate.engine.SessionFactoryImplementor;
import org.hibernate.mapping.PersistentClass;

/**
 *Persister for contains or startedBy
 * @author rssh
 */
public class RiContainsStartedByPersister extends RiTreeFunPersister
{

   public RiContainsStartedByPersister(PersistentClass persistentClass,
                            EntityRegionAccessStrategy cacheAccessStrategy,
                            SessionFactoryImplementor factory,
                            Mapping mapping)
   { super("ri_tree.ri_time_intervals_contains_strtby",persistentClass,cacheAccessStrategy,factory);
   }



}
