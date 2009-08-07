
package ua.gradsoft.jungle.persistence.ejbqlao;

import java.io.Serializable;

/**
 *Criteria, where we can set limit and offset.
 */
public interface PagingCriteria extends Serializable
{

    /**
     * @return offset, from which read.
     */
    public int getOffset();
    
    /** 
     * @return limit to read.
     */
    public int getLimit();
}
