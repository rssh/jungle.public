
package ua.gradsoft.jungle.persistence.ejbqlao;

/**
 *Criteria, where we can set limit and offset.
 */
public interface PagingCriteria {

    /**
     * @return offset, from which read.
     */
    public int getOffset();
    
    /** 
     * @return limit to read.
     */
    public int getLimit();
}
