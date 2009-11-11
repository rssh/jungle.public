package ua.gradsoft.jungle.persistence.ejbqlao;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import ua.gradsoft.jungle.persistence.ejbqlao.SqlPositionalQueryWithParams;


public class NamedToPositionalParamsSqlTransformer
{

  /**
   * translate ejb/sql 'native sql' query with named parameters to 'really native'
   *  jdbc query with positional parameters.
   * (note - this class does not touch options)
   * @param nQuery - query with named parameters.
   * @param namedParameters - parameters.
   * @return sql query with positions parameters.
   */
  public static SqlPositionalQueryWithParams translate(String nQuery, Map<String,Object> namedParameters )
  { 
    StringBuilder sb = new StringBuilder();
    List<Object> positionParameters = new ArrayList<Object>();
    String[] ejbQueryParts = nQuery.split("'");
    boolean inQuote=false;
    for(int i=0; i<ejbQueryParts.length; ++i){
        inQuote=((i%2)==1);
        if (inQuote) {
            sb.append("'");
            sb.append(ejbQueryParts[i]);
            sb.append("'");
        }else{
            substituteNamedParametersInPart(0,ejbQueryParts[i],sb,positionParameters,namedParameters);
        }    
    }
    SqlPositionalQueryWithParams retval = new SqlPositionalQueryWithParams(sb.toString(),positionParameters);
    return retval;
  }
  
  /**
   * substitue named parameters in string part, where we know that part does not contains string literals.
   */
  private static void substituteNamedParametersInPart(int index, String part, StringBuilder qb, List<Object> posParams, Map<String,Object> namedParams)
  {
    int sqlPositionParameterIndex=1;
    int varIndex=index;
    char[] arr = part.toCharArray();
    while(varIndex < arr.length) {
      while(varIndex<arr.length && arr[varIndex]!=':') {
          qb.append(arr[varIndex]);
          ++varIndex;
      }
      if (varIndex!=arr.length) {
          // we found begin of variable, lets search for end (first non-identifier)
          boolean found=false;
          int i=++varIndex;
          for(; i<arr.length && !found; ++i) {
              if (!Character.isJavaIdentifierPart(arr[i])) {
                  found=true;
                  break;
              }
          }
          if (i==varIndex) {
             throw new IllegalArgumentException("invalid sql query: ':' without name of variable");
          }
          String varName = part.substring(varIndex,i);
          Object varValue = namedParams.get(varName);
          if (varValue==null) {
              throw new IllegalArgumentException("Sql valiable '"+varName+"' does not exists");
          }
          qb.append("?");
          //qb.append(sqlPositionParameterIndex);
          posParams.add(varValue);
          ++sqlPositionParameterIndex;
          varIndex=i;
      }
    }
  }

}
