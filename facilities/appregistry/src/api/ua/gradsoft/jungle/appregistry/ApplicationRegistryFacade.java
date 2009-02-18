package ua.gradsoft.jungle.appregistry;

/**
 * facade for using application registry.
 **/
public interface ApplicationRegistryFacade
{

   boolean isRegistered(String appname);

   /**
    * try to find application record. If one is not found
    **/
   ApplicationRecord  find(String appname)
                                     throws ApplicationRecordNotFoundException;

   /**
    * register application, if it was not registered yet.
    * when such application exists - throws ApplicationAlreadyExist exception
    **/
   void  register(String name, String version, String description)
                                     throws ApplicationRecordAlreadyExistsException;

   /**
    * update application record if it was registered
    **/
   void  update(String name, String version, String description)
                                     throws ApplicationRecordNotFoundException;

   /**
    * remove application record with given name. If application record with such name
    * does not exists - do nothing.
    * @param name - name to remove;
    */
   void remove(String name);

}
