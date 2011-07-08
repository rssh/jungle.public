
package ua.gradsoft.hibernateplugin.ritree;

import ua.gradsoft.jungle.persistence.ritree.RiInterval;

/**
 * Interface, which implemented by all 'Fake' entities
 *  of RI-Tree.
 */
public interface RiFakeEntity {

    RiInterval getInterval();
    
    void setInterval(RiInterval ri);
    
    RiFakeEntity createEmpty();
    
}
