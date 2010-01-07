package ua.gradsoft.jungle.persistence.ejbqlao.util;

import java.io.Serializable;

/**
 *Utility pair class
 */
public class Pair<T1, T2> implements Serializable
{

    public Pair() {};
    
    public Pair(T1 frs, T2 snd)
    {
      frs_=frs;
      snd_=snd;
    }

    public T1 getFrs() {
        return frs_;
    }

    public void setFrs(T1 frs) {
        frs_ = frs;
    }

    public T2 getSnd() {
        return snd_;
    }

    public void setSnd(T2 snd) {
        snd_ = snd;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Pair<T1, T2> other = (Pair<T1, T2>) obj;
        if (this.frs_ != other.frs_ && (this.frs_ == null || !this.frs_.equals(other.frs_))) {
            return false;
        }
        if (this.snd_ != other.snd_ && (this.snd_ == null || !this.snd_.equals(other.snd_))) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 89 * hash + (this.frs_ != null ? this.frs_.hashCode() : 0);
        hash = 89 * hash + (this.snd_ != null ? this.snd_.hashCode() : 0);
        return hash;
    }

    @Override
    public String toString()
    {
     StringBuilder sb=new StringBuilder();
     sb.append("(");
     sb.append(frs_==null? "null" : frs_.toString());
     sb.append(", ");
     sb.append(snd_==null? "null" : snd_.toString());
     sb.append(")");
     return sb.toString();
    }


    private T1 frs_;
    private T2 snd_;
}
