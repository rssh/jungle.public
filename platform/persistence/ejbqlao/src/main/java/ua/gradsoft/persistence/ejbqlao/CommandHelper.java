package ua.gradsoft.persistence.ejbqlao;

/**
 *Helper for update commands
 * @author rssh
 */
public interface CommandHelper<T> {

    public QueryParams getQueryParams(T command);

}
