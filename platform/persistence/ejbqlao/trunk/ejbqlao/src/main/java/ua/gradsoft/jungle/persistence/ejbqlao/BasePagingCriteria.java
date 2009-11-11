package ua.gradsoft.jungle.persistence.ejbqlao;

/**
 *Base criteria for paging
 * @author zvetik
 */
public class BasePagingCriteria implements PagingCriteria
{

    public int getOffset()
    { return offset_; }

    public void setOffset(int offset)
    {
      offset_=offset;
    }

    public int getLimit() {
        return limit_;
    }

    public void setLimit(int limit) {
        limit_ = limit;
    }


    private int offset_ = -1;
    private int limit_=-1;
}
