package ua.gradsoft.jungle.persistence.ritree;


import java.io.Serializable;
import java.util.Date;
import javax.persistence.Embeddable;
import javax.persistence.Transient;

/**
 * Class which represent interval.
 *  Internal, begin and end times are rеpresented as number of seconds since epoch.
 * Also alternative setterg/getter interfaces for work with java.util.Date is provided.
 * Note, that Calendar is not used, becouse RiInterval can be used on gwt platform.
 *
 * @author rssh
 */
@Embeddable
public class RiInterval implements Serializable
{

    public RiInterval()
    {}

    /**
     * Costruct by interval
     * @param begin - begin of interval as number of seconds since epoch
     * @param end - end of interval as number of seconds since epoch
     */
    public RiInterval(long begin, long end)
    {begin_=begin;
     end_=end;
    }

   /**
     * Costruct by interval
     * @param begin - begin of interval as number of seconds since epoch
     * @param end - end of interval as number of seconds since epoch
     */
    public RiInterval(Date begin, Date end)
    {
     begin_=begin.getTime()/1000;
     end_=end.getTime()/1000;
    }


    /**
     * @return begin of interval as number of seconds since epoch.
     */
    public long getBegin()
    { return begin_; }

    /**
     * set begin of interval
     * @param begin - begin of interval as number of seconds since epoch.
     */
    public void setBegin(long begin)
    { begin_=begin; }

    public long getEnd()
    { return end_; }

    public void setEnd(long end)
    { end_=end; }

    @Transient
    public Date getBeginDate()
    {
     return new Date(begin_*1000);
    }

    public void setBeginDate(Date date)
    {
      begin_=date.getTime()/1000;
    }

    @Transient
    public Date getEndDate()
    {
      return new Date(end_*1000);
    }

    public void  setEndDate(Date date)
    {
      end_=date.getTime()/1000;
    }


    @Override
    public boolean equals(Object o)
    {
        if (o instanceof RiInterval) {
            return ((RiInterval)o).begin_ == begin_ &&
                   ((RiInterval)o).end_ == end_;
        } else {
            return false;
        }
    }

    @Override
    public int hashCode()
    {
      return (int)begin_ + (int)end_;
    }

    @Override
    public String toString()
    {
      StringBuilder sb = new StringBuilder();
      sb.append("(");
      sb.append(begin_);
      sb.append(",");
      sb.append(end_);
      sb.append(")");
      return sb.toString();
    }

    private long begin_;
    private long end_;
}
