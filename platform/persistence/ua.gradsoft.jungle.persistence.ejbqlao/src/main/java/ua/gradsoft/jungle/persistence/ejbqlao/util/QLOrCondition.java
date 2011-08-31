
package ua.gradsoft.jungle.persistence.ejbqlao.util;

import java.util.List;

/**
 *
 * @author rssh
 */
public class QLOrCondition extends QLOpCondition {


    public QLOrCondition(List<QLCondition> components) {
        super(components);
    }



    @Override
    public String op() {
        return "or";
    }


    @Override
    public String emptyValue() {
        return "1=0";
    }



}
