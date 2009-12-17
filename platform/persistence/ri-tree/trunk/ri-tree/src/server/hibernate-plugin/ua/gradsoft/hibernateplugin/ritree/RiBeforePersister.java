
package ua.gradsoft.hibernateplugin.ritree;

import org.hibernate.cache.access.EntityRegionAccessStrategy;
import org.hibernate.engine.Mapping;
import org.hibernate.engine.SessionFactoryImplementor;
import org.hibernate.mapping.PersistentClass;

/**
 *Persister for RiBefore
 * @author rssh
 */
public class RiBeforePersister extends RiTreeFunPersister
{

   public RiBeforePersister(PersistentClass persistentClass,
                            EntityRegionAccessStrategy cacheAccessStrategy,
                            SessionFactoryImplementor factory,
                            Mapping mapping)
   { super("ri_tree_before",persistentClass,cacheAccessStrategy,factory);  }

}
