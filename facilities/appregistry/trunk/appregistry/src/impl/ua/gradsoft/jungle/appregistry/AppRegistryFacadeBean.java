package ua.gradsoft.jungle.appregistry;

import javax.ejb.Local;
import javax.ejb.Remote;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 * Default implementation of ApplicationRegistry.
 *Can be used as Stateless bean (mappedName='AppRegistryFacadeBean') or 
 * simple Spring bean.
 *
 *@see ua.gradsoft.jungle.appregistry.ApplicationRegistryFacade
 **/ 
@Stateless(mappedName="AppRegistryFacadeBean")
@Local(ApplicationRegistryFacade.class)
@Remote(ApplicationRegistryFacade.class)
public class AppRegistryFacadeBean implements ApplicationRegistryFacade
{

    public ApplicationRecord find(String appname) throws ApplicationRecordNotFoundException {
       ApplicationRecord retval = entityManager_.find(ApplicationRecord.class, appname);
       if (retval==null) {
           throw new ApplicationRecordNotFoundException(appname);
       }
       return retval;
    }

    public boolean isRegistered(String appname) {
        return entityManager_.find(ApplicationRecord.class, appname)!=null;
    }

    public void register(String name, String version, String description) throws ApplicationRecordAlreadyExistsException {
        if (!isRegistered(name)) {
            ApplicationRecord r = new ApplicationRecord(name, version, description);
            entityManager_.persist(r);
        }else{
            throw new ApplicationRecordAlreadyExistsException(name);
        }
    }

    public void update(String name, String version, String description) throws ApplicationRecordNotFoundException {
        ApplicationRecord r = entityManager_.find(ApplicationRecord.class, name);
        if (r==null) {
            throw new ApplicationRecordNotFoundException(name);
        }
        r.setVersion(version);
        r.setDescription(description);
        entityManager_.persist(r);
    }

    public void remove(String name)
    {
      entityManager_.remove(name);
    }


    // local
    public void setEntityManager(EntityManager entityManager)
    {
      entityManager_=entityManager;
    }


  @PersistenceContext
  private EntityManager entityManager_;

}
