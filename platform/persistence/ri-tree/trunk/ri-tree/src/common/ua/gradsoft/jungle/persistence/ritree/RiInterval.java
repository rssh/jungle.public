package ua.gradsoft.jungle.persistence.ritree;


import java.io.Serializable;
import javax.persistence.Embeddable;

@Embeddable
public class RiInterval implements Serializable
{

    public RiInterval()
    {}

    public RiInterval(long begin, long end)
    {begin_=begin;
     end_=end;
    }


    public long getBegin()
    { return begin_; }

    public void setBegin(long begin)
    { begin_=begin; }

    public long getEnd()
    { return end_; }

    public void setEnd(long end)
    { end_=end; }


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
