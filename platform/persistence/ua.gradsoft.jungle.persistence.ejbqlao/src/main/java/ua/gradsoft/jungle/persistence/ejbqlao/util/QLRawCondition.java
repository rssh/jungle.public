
package ua.gradsoft.jungle.persistence.ejbqlao.util;

/**
 *Raw condition which provide supplies QL fragment.
 * @author rssh
 */
public class QLRawCondition extends QLCondition
{

    public QLRawCondition(String qlFragment) {
        this.qlFragment = qlFragment;
    }

    @Override
    public void outql(StringBuilder sb) {
        sb.append(qlFragment);
    }

    private String qlFragment;
}
