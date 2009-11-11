package ua.gradsoft.jungle.persistence.cluster_keys;

/**
 *Information about database cluster node.
 * @author rssh
 */
public class ClusterNodeInfo {

    public ClusterNodeInfo(int orgId, int nodeId)
    {
        orgId_=orgId;
        nodeId_=nodeId;
    }

    public int getOrgId()
    { return orgId_; }

    public int getNodeId()
    { return nodeId_; }

    private int orgId_;
    private int nodeId_;

}
