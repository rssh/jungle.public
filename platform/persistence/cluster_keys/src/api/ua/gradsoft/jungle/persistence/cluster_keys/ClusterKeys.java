package ua.gradsoft.jungle.persistence;

import java.beans.Statement;
import java.math.BigDecimal;
import java.sql.Connection;
import ua.gradsoft.jungle.persistence.cluster_keys.SequenceApi;

/**
 * Cluster keys - wrapper arround one database instance with 
 * master/master replication.
 **/ 
public class ClusterKeys
{

  public static void   initClusterInfo(int orgid, int nodeId, Connection jdbcConnection)
  {
     Statement st = jdbcConnection.createStatement();

  }


  public static ClusterInfo  getClusterInfo()
                                 throws ClusterInfoNotInitializedException;


  public static String     generateStringClusterKeyBySequence(
                                        String sequenceNumber, 
                                        Connection jdbcConnection
                                        )
  {
   return generateStringClusterKeyByLocalKey(
            SequenceApi.getInstance().getNextSequenceNumber(jdbcConnection),
            jdbcConnection);
  }

  public BigDecimal generateBigDecimalClusterKeyBySequence(
                                        String sequenceNumber, 
                                        Connection jdbcConnection
                                        );

  
  public String     generateStringClusterKeyByLocalKey(
                                         long localKey,
                                         Connection jdbcConnection); 


  public BigDecimal generateBigDecimalClusterKeyByLocalKey(
                                         long localKey,
                                         Connection jdbcConnection); 

}
