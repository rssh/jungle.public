
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
    public boolean isJoinPart() {
        return false;
    }

    public boolean isFetchAllProperties() {
        return fetchAllProperties;
    }

    public void setFetchAllProperties(boolean fetchAllProperties) {
        this.fetchAllProperties = fetchAllProperties;
    }



    @Override
    public void outql(StringBuilder sb) {
        sb.append(entityClassName);
        sb.append(" ");
        sb.append(alias);
        if (fetchAllProperties) {
            sb.append(" fetch all properties");
        }
    }


    private String entityClassName;
    private String alias;
    private boolean fetchAllProperties = false;
}
