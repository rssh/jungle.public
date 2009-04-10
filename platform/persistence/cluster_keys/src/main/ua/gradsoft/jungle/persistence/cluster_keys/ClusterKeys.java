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
import javax.persistence.EntityManager;
import ua.gradsoft.jungle.persistence.cluster_keys.impl.Base64Encoder;
import ua.gradsoft.jungle.persistence.cluster_keys.impl.MyNodeInfoBigTableJPA;
import ua.gradsoft.jungle.persistence.jdbcex.JdbcEx;
import ua.gradsoft.jungle.persistence.jdbcex.RuntimeSqlException;
import ua.gradsoft.jungle.persistence.jpaex.JdbcConnectionWrapper;
import ua.gradsoft.jungle.persistence.jpaex.JpaEx;


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

  public static void   initClusterNodeInfo(EntityManager entityManager)
  {
     initClusterNodeInfo(createDefaultClusterNodeInfo(),entityManager);
  }



  public static void   initClusterNodeInfo(ClusterNodeInfo nodeInfo,
                                           Connection jdbcConnection)
  {
    initClusterNodeInfo(nodeInfo,jdbcConnection,JdbcEx.getInstance());
  }

  public static void   initClusterNodeInfo(ClusterNodeInfo nodeInfo,
                                           EntityManager entityManager)
  {
    initClusterNodeInfo(nodeInfo,entityManager,JpaEx.getInstance());  
  }

  public static void   initClusterNodeInfo(ClusterNodeInfo nodeInfo,
                                           Connection jdbcConnection,
                                           JdbcEx jdbcEx)
  {
     Statement st = null;
     try {
       st=jdbcConnection.createStatement();
       String sql = (jdbcEx.getDialect().equals("pgsql") ?
                      "select node_id, org_id from clusterization.my_cluster_node_info"
                      :
                      "select node_id, org_id from my_cluster_node_info"
                    );
       ResultSet rs = st.executeQuery(sql);
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
                 addToMyClusterNodeInfo(nodeInfo,jdbcConnection, jdbcEx);
             }
         }
       }else{
         addToMyClusterNodeInfo(nodeInfo,jdbcConnection, jdbcEx);
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


  public static void   initClusterNodeInfo(ClusterNodeInfo nodeInfo,
                                           EntityManager entityManager,
                                           JpaEx jpaEx)
  {
    if (jpaEx.isJdbcConnectionWrapperSupported(entityManager))  {
        JdbcConnectionWrapper wr = jpaEx.getJdbcConnectionWrapper(entityManager, false);
        Connection cn = wr.getConnection();
        try {
            initClusterNodeInfo(nodeInfo,cn,jpaEx.getJdbcEx());
        } finally {
            wr.releaseConnection(cn);
        }
    } else {
        // this is non-relational query, such as google datastore.
        //  more simple: just persist ones.
        MyNodeInfoBigTableJPA jpani = new MyNodeInfoBigTableJPA(nodeInfo);
        entityManager.persist(jpani);
    }
  }



  public static ClusterNodeInfo  getClusterInfo(Connection jdbcConnection)
  {
    if (clusterNodeInfo_==null) {
        initClusterNodeInfo(jdbcConnection);
    }
    return clusterNodeInfo_;
  }

  public static ClusterNodeInfo  getClusterInfo(EntityManager entityManager)
  {
    if (clusterNodeInfo_==null) {
        initClusterNodeInfo(entityManager);
    }
    return clusterNodeInfo_;
  }

  /**
   * Generate string cluster key by sequence.
   * @param sequenceName
   * @param jdbcConnection
   * @return
   */
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

  public static String     generateStringClusterKeyBySequence(
                                        String sequenceName,
                                        EntityManager entityManager
                                        )
  {
    return generateStringClusterKeyBySequence(sequenceName,entityManager,JpaEx.getInstance());
  }

  public static String     generateStringClusterKeyBySequence(
                                        String sequenceName,
                                        EntityManager entityManager,
                                        JpaEx jpaEx
                                        )
  {
   return generateStringClusterKeyByLocalKey(
            jpaEx.getNextSequenceNumber(sequenceName, entityManager),
            entityManager);
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


  public static BigDecimal generateBigDecimalClusterKeyBySequence(
                                        String sequenceName,
                                        EntityManager entityManager
                                        )
  {
    return generateBigDecimalClusterKeyBySequence(sequenceName, entityManager,
            JpaEx.getInstance());
  }


  public static BigDecimal generateBigDecimalClusterKeyBySequence(
                                        String sequenceNumber,
                                        EntityManager entityManager,
                                        JpaEx jpaEx
                                        )
  {
    return generateBigDecimalClusterKeyByLocalKey(
                   jpaEx.getNextSequenceNumber(sequenceNumber, entityManager),
                   entityManager
            );
  }

  public static String   generateStringClusterKeyByLocalKey(
                                         long localKey,
                                         ClusterNodeInfo nodeInfo)
  {
      byte[] bytes = new byte[16];
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
      bytes[15]=(byte)(x % 256);
      return base64Encode(bytes).substring(0,23);
  }

  
  public static String   generateStringClusterKeyByLocalKey(
                                         long localKey,
                                         Connection jdbcConnection)
  {
      return generateStringClusterKeyByLocalKey(localKey,
                                   getClusterInfo(jdbcConnection));
  }

  public static String   generateStringClusterKeyByLocalKey(
                                         long localKey,
                                         EntityManager entityManager)
  {
      return generateStringClusterKeyByLocalKey(localKey,
                                   getClusterInfo(entityManager));
  }



  public static BigDecimal generateBigDecimalClusterKeyByLocalKey(
                                         long localKey,
                                         ClusterNodeInfo nodeInfo)
  {
      long firstComponent = nodeInfo.getOrgId();
      firstComponent = (firstComponent<<32)+nodeInfo.getNodeId();
      BigInteger bi = BigInteger.valueOf(firstComponent);
      bi.shiftLeft(64);
      bi=bi.add(BigInteger.valueOf(localKey));
      return new BigDecimal(bi);
  }


  public static BigDecimal generateBigDecimalClusterKeyByLocalKey(
                                         long localKey,
                                         Connection jdbcConnection)
  {
      return generateBigDecimalClusterKeyByLocalKey(localKey, getClusterInfo(jdbcConnection));
  }

  public static BigDecimal generateBigDecimalClusterKeyByLocalKey(
                                         long localKey,
                                         EntityManager entityManager)
  {
      return generateBigDecimalClusterKeyByLocalKey(localKey, getClusterInfo(entityManager));
  }



  private static String base64Encode(byte[] bytes)
  {
      return Base64Encoder.base64Encode(bytes);
  }
  
  private static void addToMyClusterNodeInfo(ClusterNodeInfo nodeInfo, Connection jdbcConnection, JdbcEx jdbcEx) throws SQLException
  {
     String sql = (jdbcEx.getDialect().equals("pgsql") ?
            "insert into clusterization.my_cluster_node_info(node_id,org_id)" +
                 " values(?,?) "
                 :
            "insert into my_cluster_node_info(node_id,org_id) values(?,?) "
            );
     PreparedStatement st1 = jdbcConnection.prepareStatement(sql);
     st1.setInt(1, nodeInfo.getNodeId());
     st1.setInt(2, nodeInfo.getOrgId());
     st1.execute();
  }

  public static final int CLUSTERKEY_STRING_LEN=22;
  public static final int CLUSTERKEY_BIGDECIMALE_PRECISION=40;
  public static final int CLUSTERKEY_BIGDECIMALE_SCALE=0;

  private static ClusterNodeInfo  clusterNodeInfo_=null;

}
