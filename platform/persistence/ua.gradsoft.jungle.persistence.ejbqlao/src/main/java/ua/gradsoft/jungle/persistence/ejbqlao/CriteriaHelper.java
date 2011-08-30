package ua.gradsoft.jungle.persistence.ejbqlao;


/**
 *Criteria helper -- how we can generate queries for criterias.
 * @author rssh
 * @see QLCriteriaHelper
 * @see JPACriteriaHelper
 */
public interface CriteriaHelper<T> {

    /**
     * create query
     * @param criteria - criteris
     * @return query which return entries, selected by criteria.
     */
    public QueryWithParams  getSelectQueryWithParams(T criteria);


    public QueryWithParams  getCountQueryWithParams(T criteria);

}
