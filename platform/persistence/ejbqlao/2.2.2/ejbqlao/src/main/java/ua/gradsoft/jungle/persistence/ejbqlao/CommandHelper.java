package ua.gradsoft.jungle.persistence.ejbqlao;


/**
 *Helper for update commands
 * @author rssh
 */
public interface CommandHelper<T> {

    public QueryWithParams getQueryWithParams(T command);

}
