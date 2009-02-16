package ua.gradsoft.persistence.ejbqlao;

/**
 *Criteria helper -- how we can generate queries for criterias.
 * @author rssh
 */
public interface CriteriaHelper<T> {

    public QueryParams  getQueryParams(T criteria);

}
