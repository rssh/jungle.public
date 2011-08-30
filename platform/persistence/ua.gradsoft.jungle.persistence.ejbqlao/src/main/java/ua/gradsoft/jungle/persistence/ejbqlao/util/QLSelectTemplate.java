
package ua.gradsoft.jungle.persistence.ejbqlao.util;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import ua.gradsoft.jungle.persistence.ejbqlao.QueryWithParams;

/**
 *Template for Select ejbql query
 * @author rssh
 */
public class QLSelectTemplate {

    public boolean isDistinct() {
        return distinct;
    }

    public void setDistinct(boolean distinct) {
        this.distinct = distinct;
    }

    public List<QLFrom> getFromParts() {
        return fromParts;
    }

    public void setFromParts(List<QLFrom> fromParts) {
        this.fromParts = fromParts;
    }

    public void addRoot(QLRoot qlRoot)
    {
       fromParts.add(qlRoot);
    }

    public void addRoot(String className, String alias)
    {
       fromParts.add(new QLRoot(className, alias));
    }

    public void addFrom(String className, String alias)
    {
       fromParts.add(new QLRoot(className, alias));
    }


    public void addRoot(Class rootClass, String alias)
    {
       addRoot(rootClass.getSimpleName(),alias);
    }

    public void addFrom(Class rootClass, String alias)
    {
       addRoot(rootClass,alias);
    }


    public void addFrom(QLFrom from)
    {
        fromParts.add(from);
    }

    public void addFrom(QLJoinType jt, String component, String alias)
    {
        fromParts.add(new QLJoinPart(jt,component,alias));
    }

    public void addFrom(QLJoinType jt, String component, String alias, QLCondition with)
    {
        fromParts.add(new QLJoinPart(jt,component,alias,with));
    }


    public boolean isOrderByDirection() {
        return orderByDirection;
    }

    public void setOrderByDirection(boolean orderByDirection) {
        this.orderByDirection = orderByDirection;
    }

    public List<String> getOrderByParts() {
        return orderByParts;
    }

    public void setOrderByParts(List<String> orderByParts) {
        this.orderByParts = orderByParts;
    }

    public void addOrderBy(String orderBy)
    {
        orderByParts.add(orderBy);
    }

    public List<String> getSelectParts() {
        return selectPart;
    }

    public void  addSelect(String field)
    {
        selectPart.add(field);
    }

    public void setSelectParts(List<String> selectPart) {
        this.selectPart = selectPart;
    }

    public List<QLCondition> getWhereParts() {
        return whereParts;
    }

    public void setWhereParts(List<QLCondition> whereParts) {
        this.whereParts = whereParts;
    }

    public void addWhere(QLCondition condition)
    {
        whereParts.add(condition);
    }

    public void addWhere(String scondition)
    {
        whereParts.add(new QLRawCondition(scondition));
    }

    public Map<String, Object> getOptions() {
        return options;
    }

    public void setOptions(Map<String, Object> options) {
        this.options = options;
    }

    public Map<String, Object> getParams() {
        return params;
    }

    public void setParams(Map<String, Object> params) {
        this.params = params;
    }



    public String generateQuery()
    {
        List<String> sfrParts = new ArrayList<String>();
        return QLGeneratorUtils.generateEjbQlStructured(selectPart, distinct, fromParts, whereParts, orderByParts, orderByDirection);
    }


    public QueryWithParams  createQueryWithParams()
    {
      return new QueryWithParams(generateQuery(),params,options);
    }


    /**
     * column index data is temporary storage for information, which we can collect
     * during quiery building.
     * You can look on it as on simple hashtable, attached to select template. 
     */
    public void putColumnIndexData(String aspect, int columnIndex, Object value)
    {
       Map<Integer,Object> aspectData = columnIndexDataStorage.get(aspect);
       if (aspectData==null) {
           aspectData = new TreeMap<Integer,Object>();
           columnIndexDataStorage.put(aspect, aspectData);
       }
       aspectData.put(columnIndex, value);
    }

    public Object getColumnIndexData(String aspect, int columnIndex)
    {
       Map<Integer,Object> aspectData = columnIndexDataStorage.get(aspect);
       if (aspectData==null) {
           return null;
       }else{
           return aspectData.get(columnIndex);
       }
    }

    public Map<Integer,Object> getColumnIndexData(String aspect)
    {
       return columnIndexDataStorage.get(aspect);
    }


    public void clearColumnIndexData()
    {
        columnIndexDataStorage.clear();
    }



    private List<String>       selectPart = new ArrayList<String>();
    private boolean            distinct = false;
    private List<QLFrom>       fromParts = new ArrayList<QLFrom>();;
    private List<QLCondition>  whereParts = new ArrayList<QLCondition>() ;
    private List<String>       orderByParts = new ArrayList<String>();
    private boolean            orderByDirection = true;
    private Map<String,Object> params = new TreeMap<String,Object>();
    private Map<String,Object> options = new TreeMap<String,Object>();

    private Map<String,Map<Integer,Object>>  columnIndexDataStorage = new TreeMap<String,Map<Integer,Object>>();

}
