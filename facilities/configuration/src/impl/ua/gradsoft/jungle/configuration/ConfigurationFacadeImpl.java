package ua.gradsoft.jungle.configuration;

@Stateless
@Remote(ConfigurationFacade)
@Local(ConfigurationFacade)
public class ConfigurationFacaseImpl extends EjbQlAccessObject
{


  public EntityManager getEntityManager()
  { return entityManager_; }
 
  @PersistenceContext 
  private EntityManager entityManager_;
}
