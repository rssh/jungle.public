
package ua.gradsoft.jungle.persistence.ejbqlao.util;

/**
 *Enum for join type.
 * @author rssh
 */
public enum QLJoinType {

    IMPLICIT {
        String prefix() { return ","; }
    },
    LEFT {
        String prefix() { return "left join"; }
    },
    LEFT_OUTER {
        String prefix() { return "left outer join"; }
    },
    RIGHT {
        String prefix() { return "right join"; }
    },
    RIGHT_OUTER {
        String prefix() { return "right outer join"; }
    }
    ;
    
    abstract String prefix();

}
