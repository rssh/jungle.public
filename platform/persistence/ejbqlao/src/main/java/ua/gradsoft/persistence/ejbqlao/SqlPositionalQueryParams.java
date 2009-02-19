package ua.gradsoft.persistence.ejbqlao;

import java.io.Serializable;
import java.util.List;

/**
 *Parameters for jdbc query
 * @author rssh
 */
public class SqlPositionalQueryParams implements Serializable
{

    public SqlPositionalQueryParams(String query, List<Object> params)
    { query_=query;
      parameters_=params;
    }

    public String getQuery()
    { return query_; }


    public List<Object> getParameters()
    { return parameters_; }

    public int getQueryTimeout()
    { return queryTimeout_; }

    private String query_;
    private List<Object> parameters_;

    int queryTimeout_;



}
