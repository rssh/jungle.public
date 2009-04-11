package ua.gradsoft.persistence.ejbqlao;

import java.util.Map;

/**
 *Class, which hold parameters, necessory for performing query.
 *Typical pattern of usage: CriteriaHelper create QueryParams by criteria.
 *@see CriteriaHelper
 */
public class QueryParams {


    /**
     * create empty query params.
     *Note, that namedParametees and options are not created autoimatically,
     *but must be set.
     */
    public QueryParams()
    { }

    public QueryParams(String query,                       
                       Map<String,Object> namedParameters,
                       Map<String,Object> options)
    {
      query_=query;   
      namedParameters_=namedParameters;
      options_=options;            
    }

    public String getQuery()
    {
      return query_;
    }

    public Map<String,Object> getNamedParameters()
    { return namedParameters_; }

    public Map<String,Object>  getOptions()
    { return options_; }

    private String query_;   
    private Map<String,Object> namedParameters_;
    private Map<String,Object> options_;

}
