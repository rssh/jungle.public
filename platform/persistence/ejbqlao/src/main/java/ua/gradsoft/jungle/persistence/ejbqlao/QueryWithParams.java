package ua.gradsoft.jungle.persistence.ejbqlao;

import java.util.Map;

/**
 *Class, which hold parameters, necessory for performing query.
 *Typical pattern of usage: CriteriaHelper create QueryWithParams by criteria.
 *@see CriteriaHelper
 */
public class QueryWithParams {


    /**
     * create empty query params.
     *Note, that namedParametees and options are not created autoimatically,
     *but must be set.
     */
    public QueryWithParams()
    { }

    public QueryWithParams(String query,
                       Map<String,Object> namedParameters,
                       Map<String,Object> options)
    {
      query_=query;   
      namedParameters_=namedParameters;
      options_=options;            
    }

    public String getQuery()
    { return query_; }

    public void  setQuery(String query)
    { query_=query; }

    public Map<String,Object> getNamedParameters()
    { return namedParameters_; }

    public void  setNamedParameters(Map<String,Object> namedParameters)
    { namedParameters_=namedParameters; }

    public Map<String,Object>  getOptions()
    { return options_; }

    public void setOptions(Map<String,Object> options)
    { options_=options; }

    private String query_;   
    private Map<String,Object> namedParameters_;
    private Map<String,Object> options_;

}
