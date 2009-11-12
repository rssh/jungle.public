package ua.gradsoft.jungle.persistence.jdbcex;


import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

/**
 *Varios utilits
 * @author rssh
 */
public class JdbcUtils {

    /**
     * Execute update statement and wrap SQLException (if one will throwec)
     *  to RuntimeSqlException
     * @param sql - code to execute
     * @param cn jdbc connection
     */
    public static void executeUpdate(String sql, Connection cn)
    {
      Statement st = null;
      try {
          st = cn.createStatement();
          st.execute(sql);
      }catch(SQLException ex){
          throw new RuntimeSqlException(ex);
      }finally{
          if (st!=null) {
              try {
                  st.close();
              }catch(SQLException ex1){
                // warn ?   TODO: think about using commons-logging here
                ;
              }
          }
      }        
    }

}
