package ua.gradsoft.jungle.persistence.jpaex;

import java.sql.Connection;

/**
 *
 * @author rssh
 */
public interface JdbcWork {

    void execute(Connection cn) throws Exception;


}
