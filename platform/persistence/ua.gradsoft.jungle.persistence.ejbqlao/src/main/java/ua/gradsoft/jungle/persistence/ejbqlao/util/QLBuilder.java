
package ua.gradsoft.jungle.persistence.ejbqlao.util;

import java.util.List;

/**
 *Builder for light-weight criteria API.
 * @author rssh
 */
public class QLBuilder {

    public static QLRoot createRoot(String entityClassName, String alias)
    {
        return new QLRoot(entityClassName, alias);
    }

    public static QLJoin createJoin(QLJoinType joinType, QLFrom parent, String parentComponent, String alias, QLCondition condition)
    {
        return new QLJoin(joinType, parent, parentComponent, alias, condition);
    }

    public static QLCondition createRawCondition(String s)
    {
        return new QLRawCondition(s);
    }

    public static QLCondition createAndCondition(List<QLCondition> args)
    {
        return new QLAndCondition(args);
    }

    public static QLSelectTemplate createSelectTemplate()
    {
        return new QLSelectTemplate();
    }

}
