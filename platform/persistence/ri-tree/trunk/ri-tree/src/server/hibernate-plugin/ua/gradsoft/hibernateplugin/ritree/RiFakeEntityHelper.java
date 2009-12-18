
package ua.gradsoft.hibernateplugin.ritree;

import ua.gradsoft.jungle.persistence.ritree.RiInterval;

/**
 *Static class for common operation on RiFakeEntity.
 * (we does not use inheritance, since hibernate analyze
 *  inheritabe of all 'pseudo-entities', so all common
 *  operations delegatede here).
 * @author rssh
 */
public class RiFakeEntityHelper {

   public static Object deepCopy(RiFakeEntity o)
   {
     RiFakeEntity retval = o.createEmpty();
     retval.setInterval(new RiInterval(o.getInterval().getBegin(),o.getInterval().getEnd()));
     return retval;
   }

}
