
package ua.gradsoft.jungle.configuration;

import java.util.Collections;
import java.util.Map;
import ua.gradsoft.jungle.persistence.ejbqlao.CriteriaHelper;
import ua.gradsoft.jungle.persistence.ejbqlao.QueryWithParams;

/**
 * Criteria Helper for selection items by id
 */
public class ConfigItemSelectorByIdCriteriaHelper 
                            implements CriteriaHelper<ConfigItemSelectorById>
{

    public QueryWithParams getSelectQueryWithParams(ConfigItemSelectorById selector) {
       String query="select item from ConfigItem item where id=:id";
       Map<String,Object> params = Collections.<String,Object>singletonMap("id", selector.getId());
       Map<String,Object> options = ConfigItemSelectorHelper.createOptions(selector);
       return new QueryWithParams(query,params,options);
    }

    public QueryWithParams getCountQueryWithParams(ConfigItemSelectorById selector) {
       String query="select count(item) from ConfigItem item where id=:id";
       Map<String,Object> params = Collections.<String,Object>singletonMap("id", selector.getId());
       Map<String,Object> options = ConfigItemSelectorHelper.createOptions(selector);
       return new QueryWithParams(query,params,options);
    }



}
