
package ua.gradsoft.jungle.persistence.ejbqlao.util;

/**
 *Abstract class for QL generator
 * @author rssh
 */
public abstract class QLComponent {

    /**
     * output part of phrase to StringBuilder;
     * @param sb
     */
    public abstract void outql(StringBuilder sb);

    /**
     * output part of phrase to String
     * @return part of ql.
     */
    public String ql()
    {
       StringBuilder sb = new StringBuilder();
       outql(sb);
       return sb.toString();
    }

}
