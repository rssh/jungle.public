
package ua.gradsoft.jungle.persistence.jpaex.impl;

import java.lang.reflect.InvocationTargetException;
import java.sql.Connection;
import ua.gradsoft.jungle.persistence.jpaex.JdbcConnectionWrapper;
import ua.gradsoft.jungle.persistence.jpaex.JdbcWork;
import ua.gradsoft.jungle.persistence.jpaex.JdbcWorkExecutor;

/**
 *Default executor of JDBC operations, build on top of JdbcConnectionWrapper
 * @author rssh
 */
public class DefaultJdbcWorkExecutor implements JdbcWorkExecutor
{

    public DefaultJdbcWorkExecutor(JdbcConnectionWrapper wrapper)
    { wrapper_=wrapper; }

    public void execute(JdbcWork work) throws InvocationTargetException {
        Connection cn = wrapper_.getConnection();
        try {
            work.execute(cn);
        }catch(Exception ex){
            throw new InvocationTargetException(ex);
        }finally{
            wrapper_.releaseConnection(cn);
        }
    }



    private JdbcConnectionWrapper wrapper_;
}
