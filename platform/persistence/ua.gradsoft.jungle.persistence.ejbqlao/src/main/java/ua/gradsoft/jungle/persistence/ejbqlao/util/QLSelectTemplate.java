
package ua.gradsoft.jungle.persistence.ejbqlao.util;

import java.util.ArrayList;
import java.util.List;

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

    public List<Pair<String, String>> getFromParts() {
        return fromParts;
    }

    public void setFromParts(List<Pair<String, String>> fromParts) {
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

    public List<String> getWhereParts() {
        return whereParts;
    }

    public void setWhereParts(List<String> whereParts) {
        this.whereParts = whereParts;
    }


    String generateQuery()
    {
        List<String> sfrParts = new ArrayList<String>();
        for(Pair<String,String> p:fromParts) {
            sfrParts.add(p.getFrs()+" "+p.getSnd());
        }
        return QLGeneratorUtils.generateEjbQl(selectPart, distinct, sfrParts, whereParts, orderByParts, orderByDirection);
    }



    private List<String> selectPart = new ArrayList<String>();
    private boolean      distinct = false;
    private List<Pair<String,String>> fromParts = new ArrayList<Pair<String,String>>();;
    private List<String>  whereParts = new ArrayList<String>() ;
    private List<String>  orderByParts = new ArrayList<String>();
    private boolean       orderByDirection = true;

}
