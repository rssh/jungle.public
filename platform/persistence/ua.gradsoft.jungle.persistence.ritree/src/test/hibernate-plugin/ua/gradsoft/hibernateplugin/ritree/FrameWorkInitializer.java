
package ua.gradsoft.hibernateplugin.ritree;

import java.io.BufferedReader;
import java.io.FileReader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.util.Map;
import java.util.TreeMap;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import ua.gradsoft.jungle.persistence.jpaex.HibernateJpaEx;
import ua.gradsoft.jungle.persistence.jpaex.JpaEx;

/**
 *aaaa
 * @author rssh
 */
public class FrameWorkInitializer {


  public static void lazyInit() throws Exception
  {
   if (!initialized_) {
     init();
   }
  }


  public static EntityManager getEntityManager()
  {
      return entityManager_;
  }

  private static void init() throws Exception {
     initJpaEx();
     initPostgresDb();
     //loadSql();
     createEntityManager();
     initialized_=true;
  }



  private static void initPostgresDb() throws Exception
  {
      Class.forName(jdbcDriver);
      Connection cn=DriverManager.getConnection(jdbcUrl,jdbcLogin,jdbcPassword);
  }


  private static void createEntityManager()
  {
    Map<String,String> properties = new TreeMap<String,String>();
    properties.put("hibernate.connection.driver_class", jdbcDriver);
    properties.put("hibernate.connection.url", jdbcUrl);
    properties.put("hibernate.connection.username", jdbcLogin);
    properties.put("hibernate.connection.password", jdbcPassword);
    EntityManagerFactory emf = Persistence.createEntityManagerFactory("testPU",properties);
    entityManager_ = emf.createEntityManager();
  }

 
  private static void loadSql() throws Exception
  {
    loadSqlScript("sql/pgsql/test/init_testdata.sql");
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

  private static String jdbcDriver = "org.postgresql.Driver";
  private static String jdbcUrl = "jdbc:postgresql://127.0.0.1:5432/ifsb";
  private static String jdbcLogin = "postgres";
  private static String jdbcPassword = "postgres";

  //private static EntityManager entityManager_;
  private static EntityManager entityManager_;
  private static boolean initialized_ = false;


}
