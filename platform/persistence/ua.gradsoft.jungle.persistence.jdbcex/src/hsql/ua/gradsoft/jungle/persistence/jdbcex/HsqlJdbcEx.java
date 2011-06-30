package ua.gradsoft.jungle.persistence.jdbcex;


import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;


public class HsqlJdbcEx extends JdbcEx
{

 /**
 *@return "pgsql"
 **/
 public String getDialect()
 { return "hsql"; }

 /**
  *@return true
  **/
 public boolean isSupportMultipleSchemas()
 {
   return true;
 }

 /**
 *@return true
 **/
 public boolean isSupportsOffsetAndLimit()
 { return true; }


  public long getNextSequenceNumber(String sequenceName, Connection cn)
  {
   checkValidSqlIdentifier(sequenceName);
   StringBuilder sb = new StringBuilder();
   sb.append("SELECT NEXT VALUE FOR ");
   sb.append(sequenceName);
   sb.append("  FROM system.sequences");
   Statement st = null;
   try {
     st = cn.createStatement();
     ResultSet rs = st.executeQuery(sb.toString());
     rs.next();
     return rs.getLong(1);
   }catch(SQLException ex){
       throw new RuntimeSqlException(ex);
   }finally{
       if (st!=null) {
          try {
           st.close();
          }catch(SQLException ex){
              // do nothing.
          }
       }
   }
  }

  /**
   * create sequence.  Note, that hsql sentences are integers, not longs.
   */
  public void createSequence(String name, long initial, int increment, Connection cn)
  {
      checkValidSqlIdentifier(name);
      if (initial >= Integer.MAX_VALUE) {
          throw new IllegalArgumentException("initial value is too big for hsql sequence");
      }
      StringBuilder sb = new StringBuilder();
      sb.append("create sequence ");
      sb.append(name);
      sb.append(" start with ");
      sb.append(Long.toString(initial));
      sb.append(" increment by ");
      sb.append(Integer.toString(increment));
      sb.append(";");
      JdbcUtils.executeUpdate(sb.toString(), cn);
  }


 /**
  * drop sequence with name <code> name</code>.
  * Note, that in most implementation this is
  * DDL statement, which must live in own transaction.
  **/
  public void dropSequence(String name, Connection cn)
  {
      checkValidSqlIdentifier(name);
      StringBuilder sb = new StringBuilder();
      sb.append("drop sequence ");
      sb.append(name);
      sb.append(";");
      JdbcUtils.executeUpdate(name, cn);
  }



}
