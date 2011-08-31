
package ua.gradsoft.jungle.persistence.ejbqlao.util;

import java.util.List;

/**
 *Condition which
 * @author rssh
 */
public class QLAndCondition extends QLOpCondition
{


    public QLAndCondition(List<QLCondition> components) {
        super(components);
    }



    @Override
    public String op() {
        return "and";
    }


    @Override
    public String emptyValue() {
        return "1=1";
    }




}
