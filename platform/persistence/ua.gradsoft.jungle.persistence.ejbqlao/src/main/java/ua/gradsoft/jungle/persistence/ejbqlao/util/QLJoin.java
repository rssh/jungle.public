
package ua.gradsoft.jungle.persistence.ejbqlao.util;

/**
 *Join component
 * @author rssh
 */
public class QLJoin extends QLFrom
{

    public QLJoin(QLJoinType joinType, QLFrom parent, String parentComponent, String alias, QLCondition withCondition) {
        this.joinType = joinType;
        this.parent = parent;
        this.parentComponent = parentComponent;
        this.alias = alias;
        this.withCondition = withCondition;
    }

    public QLJoin(QLJoinType joinType, QLFrom parent, String parentComponent, String alias) {
        this(joinType, parent, parentComponent, alias, null);
    }

    @Override
    public boolean isJoinPart() {
        return parent.isJoinPart();
    }



    @Override
    public String getAlias() {
        return alias;
    }

    @Override
    public void outql(StringBuilder sb) {
        parent.outql(sb);
        sb.append(" ");
        sb.append(joinType.prefix());
        sb.append(" ");
        sb.append(parentComponent);
        sb.append(" as ");
        sb.append(alias);
        if (withCondition!=null) {
            sb.append(" with ");
            withCondition.outql(sb);
        }
    }

    private QLJoinType joinType;
    private QLFrom     parent;
    private String     parentComponent;
    private String     alias;
    private QLCondition  withCondition;

}
