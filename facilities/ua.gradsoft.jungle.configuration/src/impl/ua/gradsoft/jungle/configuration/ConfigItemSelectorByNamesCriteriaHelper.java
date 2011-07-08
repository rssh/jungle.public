
package ua.gradsoft.jungle.configuration;

import java.util.Map;
import java.util.TreeMap;
import ua.gradsoft.jungle.persistence.ejbqlao.CriteriaHelper;
import ua.gradsoft.jungle.persistence.ejbqlao.QueryWithParams;

/**
 * CriteriaHelper for selection of items by names
 */
public class ConfigItemSelectorByNamesCriteriaHelper implements CriteriaHelper<ConfigItemSelectorByNames>
{

    public QueryWithParams getCountQueryWithParams(ConfigItemSelectorByNames selector) {
        return buildQuery(true,selector);
    }

    public QueryWithParams getSelectQueryWithParams(ConfigItemSelectorByNames selector) {
        return buildQuery(false,selector);
    }

    private QueryWithParams buildQuery(boolean isCount, ConfigItemSelectorByNames selector)
    {
     StringBuilder query = new StringBuilder();
     query.append("select ");
     query.append(isCount ? "count(item)" : "item");
     query.append(" from ConfigItem item ");
     Map<String,Object> params = new TreeMap<String,Object>();
     if (selector.getAppName()!=null || selector.getItemName()!=null) {
         query.append("where ");
     }
     boolean firstCondition=true;
     if (selector.getAppName()!=null) {
         query.append(" item.appName = :appName ");
         params.put("appName", selector.getAppName());
         firstCondition=false;
     }
     if (selector.getItemName()!=null) {
         if (!firstCondition) {
             query.append(" and ");
         }
         query.append("item.name = :itemName");
         params.put("itemName", selector.getItemName());
         firstCondition = false;
     }
     Map<String,Object> options = ConfigItemSelectorHelper.createOptions(selector);
     return new QueryWithParams(query.toString(),params,options);
    }

}
