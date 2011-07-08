
package ua.gradsoft.jungle.persistence.cluster_keys.impl;

import javax.persistence.Entity;
import javax.persistence.Id;
import ua.gradsoft.jungle.persistence.cluster_keys.ClusterNodeInfo;

/**
 *JPA class for myNodeInfo for google bigtable.
 * @author rssh
 */
@Entity
public class MyNodeInfoBigTableJPA {

    public MyNodeInfoBigTableJPA()
    { info_=0L; }

    public MyNodeInfoBigTableJPA(ClusterNodeInfo ni)
    {
      info_ = (((long)ni.getNodeId() << 32) | (long)ni.getOrgId());
    }

    @Id
    public Long getInfo()
    { return info_; }

    public void setInfo(Long info)
    { info_ = info; }

    
    private Long info_;

}
