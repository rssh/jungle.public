package ua.gradsoft.jungle.persistence.jdbcex;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class PgsqlJdbcEx extends JdbcEx
{

 /**
 *@return "pgsql"
 **/
 public String getDialect()
 { return "pgsql"; }

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
    PreparedStatement st=null;
    try {
      st = cn.prepareStatement("select nextval(?)");
      st.setString(1,sequenceName);
      ResultSet rs = st.executeQuery();
      return rs.getLong(1);
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

  public void createSequence(String name, long initial, int increment, Connection cn)
  {
      checkValidSqlIdentifier(name);
      StringBuilder sb = new StringBuilder();
      sb.append("create sequence ");
      sb.append(name);
      sb.append(" increment by ");
      sb.append(Integer.toString(increment));
      sb.append(" start with ");
      sb.append(Long.toString(initial));
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
