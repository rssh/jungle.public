
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

    public List<String> getSelectPart() {
        return selectPart;
    }

    public void  addSelectField(String field)
    {
        selectPart.add(field);
    }

    public void setSelectPart(List<String> selectPart) {
        this.selectPart = selectPart;
    }

    public List<QLCondition> getWhereParts() {
        return whereParts;
    }

    public void setWhereParts(List<QLCondition> whereParts) {
        this.whereParts = whereParts;
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


    private List<String>       selectPart = new ArrayList<String>();
    private boolean            distinct = false;
    private List<QLFrom>       fromParts = new ArrayList<QLFrom>();;
    private List<QLCondition>  whereParts = new ArrayList<QLCondition>() ;
    private List<String>       orderByParts = new ArrayList<String>();
    private boolean            orderByDirection = true;
    private Map<String,Object> params = new TreeMap<String,Object>();
    private Map<String,Object> options = new TreeMap<String,Object>();

}
