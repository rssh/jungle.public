package ua.gradsoft.jungle.gwt.util.client;

/**
 * *Utility class with null-safe operations.
 */
public class NullSafeUtils
{

    /**
     * Are objects x and y are equals ?
     *<pre>
     *  null, null -> true,
     *  null, non-null -> false,
     *  non-null, null -> false,
     *  non-null, non-nul -> equals(x,y)
     *</pre>
     */
    public static boolean areEquals(Object x, Object y)
    {
      if (x==null) {
          return y==null;
      }else if (y==null) {
          return false;
      }else{
          return x.equals(y);
      }
    }

    /*
     * Are objects x and y (where x or y can be null) are different ?
     */
    public static boolean areDifferent(Object x, Object y)
    {
      return !areEquals(x,y);
    }


}
