
package ua.gradsoft.jungle.persistence.ejbqlao;

import java.util.Map;

/**
 *Helper for paginc criteria
 * @author zvetik
 */
public class PagingCriteriaHelper
{

    public static void  setOptions(PagingCriteria criteria, Map<String,Object> options)
    {
      if (criteria.getOffset() > 0) {
          options.put("firstResult", criteria.getOffset());
      }  
      if (criteria.getLimit() > 0) {
          options.put("maxResults", criteria.getLimit());
      }
    }

}
