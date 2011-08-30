
package ua.gradsoft.jungle.persistence.ejbqlao.util;

/**
 * From component for ejb/ql
 * @author rssh
 */
public abstract class QLFrom extends QLComponent
{


    public abstract void outql(StringBuilder sb);

    /**
     * get alias for string.
     * @return
     */
    public abstract String getAlias();

    /**
     * true, if this is unfinished join part, which
     * require predesc
     */
    public abstract boolean isJoinPart();

    /**
     * create join
     */
    public QLFrom join(QLJoinType joinType, String component, String alias)
    {
        return new QLJoin(joinType, this, component, alias);
    }

}
