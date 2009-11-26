package ua.gradsoft.jungle.localization;

import java.sql.Connection;
import java.sql.DriverManager;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

/**
 * Singleton, which perform action, needed for setup and initializing
 * of test database. init action must be called before an operation, but
 * once and only once. Usual : call of lazyInit from test setUp method
 **/
public class TestInfrastructureSingleton
{




  public static void lazyInit() throws Exception
  {
   if (!initialized_) {
     init();
   }
  }

  public static LocalizationFacadeImpl getLocalizationFacadeImpl()
  {
    return localizationFacadeImpl_;
  }
  

  private static void init() throws Exception {
     initInMemoryDb();
     createEntityManager();
     createLocalizationFacade();
     initialized_=true;
  }

  private static void initInMemoryDb() throws Exception
  {
      Class.forName("org.hsqldb.jdbcDriver");
      Connection cn=DriverManager.getConnection("jdbc:hsqldb:mem:test","sa","");
  }

  private static void createEntityManager()
  {
    EntityManagerFactory emf = Persistence.createEntityManagerFactory("testPU");
    entityManager_ = emf.createEntityManager();
  }
  
  private static void createLocalizationFacade()
  {
    localizationFacadeImpl_ = new LocalizationFacadeImpl();
    localizationFacadeImpl_.setEntityManager(entityManager_);
  }

  //private static EntityManager entityManager_;
  private static EntityManager entityManager_;
  private static LocalizationFacadeImpl localizationFacadeImpl_;
  private static boolean initialized_ = false;
}
