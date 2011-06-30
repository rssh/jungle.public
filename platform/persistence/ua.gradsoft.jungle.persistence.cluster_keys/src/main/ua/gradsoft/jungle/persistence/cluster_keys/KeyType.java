package ua.gradsoft.jungle.persistence.cluster_keys;

/**
 *Type of entity key.
 * @author rssh
 */
public enum KeyType {

    /**
     * Cluster database key, which is 128-bit key, where 1-st 64 bit is organization
     *and node in cluster, last 64 bit - sequence itself.
     *Approprioative SQL columm type must be NUMBEIC(40,0) or CHAR(22).
     */
    CLUSTER,
    
    /**
     * Ordinary database keys.  
     */
    ORDINARY

}
