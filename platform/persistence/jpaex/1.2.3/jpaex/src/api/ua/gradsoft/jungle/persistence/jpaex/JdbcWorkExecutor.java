package ua.gradsoft.jungle.persistence.jpaex;

import java.lang.reflect.InvocationTargetException;

/**
 *Executoe, which allows execution of jdbc work
 * @author rssh
 */
public interface JdbcWorkExecutor {

    void execute(JdbcWork work) throws InvocationTargetException;

}
