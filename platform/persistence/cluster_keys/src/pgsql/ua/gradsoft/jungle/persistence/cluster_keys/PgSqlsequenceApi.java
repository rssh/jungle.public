package ua.gradsoft.jungle.persistence.cluster_keys;

public class PgSqlSequenceApi extends SequenceApi
{

  public long getNextSequenceNumber(String sequenceName, Connection cn)
  {
    PreparedStatement st = cn.prepareStatement("select nextval(?)");
    st.setString(1,sequenceName);
    ResultSet rs = st.executeQuery();
    long retval=rs.getLong(1);
    rs.close();
    return retval;
  }



}
