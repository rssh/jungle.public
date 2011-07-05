
package ua.gradsoft.jungle.persistence.ejbqlao.util;

import java.io.Serializable;

/**
 *Utility class for triplet
 * @author rssh
 */
public class Triplet<T1,T2,T3> implements Serializable
{

    public Triplet()
    {}
    
    public Triplet(T1 frs, T2 snd, T3 thr)
    {
     frs_=frs;
     snd_=snd;
     thr_=thr;
    }

    public T1 getFrs() {
        return frs_;
    }

    public void setFrs(T1 frs) {
        this.frs_ = frs;
    }

    public T2 getSnd() {
        return snd_;
    }

    public void setSnd(T2 snd) {
        this.snd_ = snd;
    }

    public T3 getThr() {
        return thr_;
    }

    public void setThr(T3 thr) {
        this.thr_ = thr;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Triplet<T1, T2, T3> other = (Triplet<T1, T2, T3>) obj;
        if (this.frs_ != other.frs_ && (this.frs_ == null || !this.frs_.equals(other.frs_))) {
            return false;
        }
        if (this.snd_ != other.snd_ && (this.snd_ == null || !this.snd_.equals(other.snd_))) {
            return false;
        }
        if (this.thr_ != other.thr_ && (this.thr_ == null || !this.thr_.equals(other.thr_))) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 83 * hash + (this.frs_ != null ? this.frs_.hashCode() : 0);
        hash = 83 * hash + (this.snd_ != null ? this.snd_.hashCode() : 0);
        hash = 83 * hash + (this.thr_ != null ? this.thr_.hashCode() : 0);
        return hash;
    }

    private T1 frs_;
    private T2 snd_;
    private T3 thr_;
}
