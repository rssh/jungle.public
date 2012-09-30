
package ua.gradsoft.jungle.persistence.ejbqlao.util;

/**
 *From componend, described by raw string.
 *  Value of raw string is what we will see in ejb/ql, alias is not used for generation.
 * @author rssh
 */
public class QLRawFrom extends QLFrom
{

    public QLRawFrom(String value) {
        this.value = value;
        this.alias = value;
    }


    public QLRawFrom(String value, String alias) {
        this.value = value;
        this.alias = alias;
    }

    @Override
    public String getAlias() {
        return alias;
    }

    @Override
    public boolean isJoinPart() {
        return false;
    }



    @Override
    public void outql(StringBuilder sb) {
        sb.append(value);
    }



    private String value;
    private String alias;
}
