package ua.gradsoft.jungle.persistence.cluster_keys;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.net.Inet4Address;
import java.net.UnknownHostException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import ua.gradsoft.jungle.persistence.cluster_keys.impl.Base64Encoder;
import ua.gradsoft.jungle.persistence.jdbcex.JdbcEx;
import ua.gradsoft.jungle.persistence.jdbcex.RuntimeSqlException;


/**
 * Cluster keys - API for generating keys in database with master/master
 * replication.
 * Each key consists from 128 bits, where first 64 bits are coordinates
 * of cluster node, seconds - normal 64-bit key (as usual long database).
 **/ 
public class ClusterKeys
{

  /**
   * create default cluster node info, whith node_id same as
   * localhist ip address and org_id as constant 1.
   *   If does not created -
   * @return created clusterNodeIndo
   */
  public static ClusterNodeInfo  createDefaultClusterNodeInfo()  
  {

      Inet4Address myAddr = null;
      try {
          myAddr=(Inet4Address)Inet4Address.getLocalHost();
      }catch(UnknownHostException ex){
          throw new IllegalStateException("Can't resolve localhost",ex);
      }
      if (myAddr.isLoopbackAddress()) {
          throw new IllegalStateException("getLocalHost returns loopback address, check network initialization");
      }
      byte[] bytes = myAddr.getAddress();
      if (bytes.length!=4) {
          throw new IllegalStateException("IP address must be 4 bytes");
      }
      int clusterNodeId = ((((bytes[0]<<8)+bytes[1])<<8)+bytes[2])<<8+bytes[3];
      int orgId = 1;
      return new ClusterNodeInfo(orgId,clusterNodeId);
  }


  public static void   initClusterNodeInfo(Connection jdbcConnection)
  {
     initClusterNodeInfo(createDefaultClusterNodeInfo(),jdbcConnection);
  }


  public static void   initClusterNodeInfo(ClusterNodeInfo nodeInfo,
                                           Connection jdbcConnection)
  {
    initClusterNodeInfo(nodeInfo,jdbcConnection,JdbcEx.getInstance());
  }

  public static void   initClusterNodeInfo(ClusterNodeInfo nodeInfo,
                                           Connection jdbcConnection,
                                           JdbcEx jdbcEx)
  {
     Statement st = null;
     try {
       st=jdbcConnection.createStatement();
       ResultSet rs = st.executeQuery("select node_number, org_id from clusterization.my_cluster_node_info");
       if (rs.next()) {
         // check for same org_id and node_id.  insert if not found.
         int nodeId = rs.getInt(1);
         int orgId = rs.getInt(2);
         if (nodeId!=nodeInfo.getNodeId() || orgId!=nodeInfo.getOrgId()) {
             // try to find or add our node id
             boolean found=false;
             while(rs.next() && !found) {
                 nodeId = rs.getInt(1);
                 orgId = rs.getInt(2);
                 if (nodeId==nodeInfo.getNodeId() && orgId==nodeInfo.getOrgId()) {
                     found = true;
                     break;
                 }
             }
             if (!found) {
                 addToMyClusterNodeInfo(nodeInfo,jdbcConnection);
             }
         }
       }else{
         addToMyClusterNodeInfo(nodeInfo,jdbcConnection);
       }
     }catch(SQLException ex){
         throw new RuntimeSqlException(ex);
     }finally{
         if (st!=null) {
            try{
             st.close();
            }catch(SQLException ex1){
                /*do nothing*/;
            }
         }
     }
     clusterNodeInfo_=nodeInfo;
  }


  public static ClusterNodeInfo  getClusterInfo(Connection jdbcConnection)
  {
    if (clusterNodeInfo_==null) {
        initClusterNodeInfo(jdbcConnection);
    }
    return clusterNodeInfo_;
  }


  public static String     generateStringClusterKeyBySequence(
                                        String sequenceName,
                                        Connection jdbcConnection
                                        )
  {
    return generateStringClusterKeyBySequence(sequenceName,jdbcConnection,JdbcEx.getInstance());
  }


  public static String     generateStringClusterKeyBySequence(
                                        String sequenceName,
                                        Connection jdbcConnection,
                                        JdbcEx jdbcEx
                                        )
  {
   return generateStringClusterKeyByLocalKey(
            jdbcEx.getNextSequenceNumber(sequenceName, jdbcConnection),
            jdbcConnection);
  }

  public static BigDecimal generateBigDecimalClusterKeyBySequence(
                                        String sequenceName,
                                        Connection jdbcConnection
                                        )
  {
    return generateBigDecimalClusterKeyBySequence(sequenceName, jdbcConnection,
            JdbcEx.getInstance());
  }


  public static BigDecimal generateBigDecimalClusterKeyBySequence(
                                        String sequenceNumber, 
                                        Connection jdbcConnection,
                                        JdbcEx jdbcEx
                                        )
  {
    return generateBigDecimalClusterKeyByLocalKey(
                   jdbcEx.getNextSequenceNumber(sequenceNumber, jdbcConnection),
                   jdbcConnection
            );
  }

  
  public static String   generateStringClusterKeyByLocalKey(
                                         long localKey,
                                         Connection jdbcConnection)
  {
      byte[] bytes = new byte[16];
      ClusterNodeInfo nodeInfo = getClusterInfo(jdbcConnection);
      int x = nodeInfo.getOrgId();
      bytes[0]=(byte)(x>>24);
      bytes[1]=(byte)(x>>16);
      bytes[2]=(byte)(x>>8);
      bytes[3]=(byte)x;
      x = nodeInfo.getNodeId();
      bytes[4]=(byte)(x>>24);
      bytes[5]=(byte)(x>>16);
      bytes[6]=(byte)(x>>8);
      bytes[7]=(byte)(x);
      x = (int)(localKey>>32);
      bytes[8]=(byte)(x>>24);
      bytes[9]=(byte)(x>>16);
      bytes[10]=(byte)(x>>8);
      bytes[11]=(byte)(x);
      x = (int)(localKey & 0xFFFFFFFF);
      bytes[12]=(byte)(x>>24);
      bytes[13]=(byte)(x>>16);
      bytes[14]=(byte)(x>>8);
      bytes[14]=(byte)(x);
      return base64Encode(bytes);
  }


  public static BigDecimal generateBigDecimalClusterKeyByLocalKey(
                                         long localKey,
                                         Connection jdbcConnection)
  {
      ClusterNodeInfo nodeInfo = getClusterInfo(jdbcConnection);
      long firstComponent = nodeInfo.getOrgId();
      firstComponent = (firstComponent<<32)+nodeInfo.getNodeId();
      BigInteger bi = BigInteger.valueOf(firstComponent);
      bi.shiftLeft(64);
      bi=bi.add(BigInteger.valueOf(localKey));
      return new BigDecimal(bi);
  }


  private static String base64Encode(byte[] bytes)
  {
      return Base64Encoder.base64Encode(bytes);
  }
  
  private static void addToMyClusterNodeInfo(ClusterNodeInfo nodeInfo, Connection jdbcConnection) throws SQLException
  {
     PreparedStatement st1 = jdbcConnection.prepareStatement(
                 "insert into clusterization.my_cluster_node_info(node_number,org_id)" +
                 " values(?,?) "
                 );
     st1.setInt(1, nodeInfo.getNodeId());
     st1.setInt(2, nodeInfo.getOrgId());
     st1.execute();
  }


  private static ClusterNodeInfo  clusterNodeInfo_=null;

}
