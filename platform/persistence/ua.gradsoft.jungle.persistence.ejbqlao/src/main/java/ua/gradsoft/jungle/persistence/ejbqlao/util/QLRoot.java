
package ua.gradsoft.jungle.persistence.ejbqlao.util;

/**
 *Root clause of from.
 * @author rssh
 */
public class QLRoot extends QLFrom
{

    public QLRoot(String entityClassName, String alias) {
        this.entityClassName = entityClassName;
        this.alias = alias;
    }

    @Override
    public String getAlias() {
        return alias;
    }

    @Override
    public void outql(StringBuilder sb) {
        sb.append(entityClassName);
        sb.append(" ");
        sb.append(alias);
    }


    private String entityClassName;
    private String alias;
}
