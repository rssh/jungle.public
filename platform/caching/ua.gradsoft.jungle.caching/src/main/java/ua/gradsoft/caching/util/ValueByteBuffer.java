package ua.gradsoft.caching.util;

import java.io.Serializable;
import java.util.Arrays;

/**
 *Value buffer, which have overloaded
 */
public class ValueByteBuffer implements Serializable
{

    public ValueByteBuffer(byte[] v)
    { v_=v; }

    @Override
    public boolean equals(Object o)
    {
      if ( o instanceof ValueByteBuffer) {
          byte[] v = ((ValueByteBuffer)o).v_;
          return Arrays.equals(v_, v);
      } else {
          return false;
      }

    }

    @Override
    public int hashCode()
    {
        return Arrays.hashCode(v_);
    }

    private byte[] v_;

}
