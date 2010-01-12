
package ua.gradsoft.jungle.persistence.ritree;

import java.util.Date;

/**
 *static class for ri-related utils.
 * @author rssh
 */
public class RiUtils {

    public static Date longToDate(long value)
    {
        return new Date(value*1000);
    }
    
    public static long DateToLong(Date d)
    {
        return d.getTime()/1000;
    }

}
