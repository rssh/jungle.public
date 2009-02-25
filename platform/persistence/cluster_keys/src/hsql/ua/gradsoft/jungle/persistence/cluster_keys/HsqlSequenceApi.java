package ua.gradsoft.jungle.persistence.cluster_keys;

public class HsqlSequeceApi extends SequenceApi
{

  public long generateNextSequenceValue(String sequenceName, Connection cn)
  {
   if (!sequenceName.match("([a-z]|[A-Z]|_|[0-9])+")) {
     throw new IllegalArgumentException("invalid sequence name:"+sequenceName);
   }
   StringBuilder sb = new StringBuilder();
   sb.append("SELECT [...,] NEXT VALUE FOR ");
   sb.append(sequenceName);
   sb.append("  FROM my_cluster_info");
   Statement st = cn.createStatement(sb.toString());
   ResultSet rs = st.executeQuery(st); 
   rs.next();
   return st.getLong(1); 
  }

}
