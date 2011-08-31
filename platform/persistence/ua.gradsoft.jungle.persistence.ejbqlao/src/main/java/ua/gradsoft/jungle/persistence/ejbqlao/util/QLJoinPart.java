
package ua.gradsoft.jungle.persistence.ejbqlao.util;

/**
 *Part of join (without root expression), which
 *  an be added to from clause of select template.
 * @author rssh
 */
public class QLJoinPart extends QLFrom
{

    public QLJoinPart(QLJoinType joinType, String parentComponent, String alias, QLCondition withCondition) {
        this.joinType = joinType;
        this.parentComponent = parentComponent;
        this.alias = alias;
        this.withCondition = withCondition;
    }

   public QLJoinPart(QLJoinType joinType, String parentComponent, String alias) {
        this(joinType, parentComponent, alias, null);
    }

    @Override
    public String getAlias() {
        return alias;
    }

    @Override
    public boolean isJoinPart() {
        return true;
    }




    @Override
    public void outql(StringBuilder sb) {
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

    public QLJoinType getJoinType() {
        return joinType;
    }

    public void setJoinType(QLJoinType joinType) {
        this.joinType = joinType;
    }

    public String getParentComponent() {
        return parentComponent;
    }

    public void setParentComponent(String parentComponent) {
        this.parentComponent = parentComponent;
    }

    public QLCondition getWithCondition() {
        return withCondition;
    }

    public void setWithCondition(QLCondition withCondition) {
        this.withCondition = withCondition;
    }


    private QLJoinType joinType;
    private String     parentComponent;
    private String     alias;
    private QLCondition  withCondition;


}
