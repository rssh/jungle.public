package ua.gradsoft.jungle.localization;

import java.io.BufferedReader;
import java.io.FileReader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import ua.gradsoft.jungle.persistence.jpaex.HibernateJpaEx;
import ua.gradsoft.jungle.persistence.jpaex.JpaEx;

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

  public static EntityManager getEntityManager()
  {
      return entityManager_;
  }

  private static void init() throws Exception {
     initJpaEx();
     initInMemoryDb();
     loadSql();
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

  private static void loadSql() throws Exception
  {
    loadSqlScript("sql/hsql/create_localization_hsql.sql");
    loadSqlScript("sql/hsql/init_localization_hsql.sql");
    loadSqlScript("sql/hsql/test/create_testdata.sql");
    loadSqlScript("sql/hsql/test/init_testdata.sql");
  }
  
  private static void loadSqlScript(String fname) throws Exception
  {
      BufferedReader in = new BufferedReader(new FileReader(fname));
      StringBuilder sb = new StringBuilder();
      String s=null;
      while((s=in.readLine())!=null) {
          sb.append(s+"\n");
      }
      in.close();
      Connection cn=DriverManager.getConnection("jdbc:hsqldb:mem:test","sa","");
      Statement st = cn.createStatement();
      st.executeUpdate(sb.toString());
      st.close();
  }

  private static void initJpaEx()
  {
     JpaEx.setInstance(new HibernateJpaEx()); 
  }

  //private static EntityManager entityManager_;
  private static EntityManager entityManager_;
  private static LocalizationFacadeImpl localizationFacadeImpl_;
  private static boolean initialized_ = false;
}
