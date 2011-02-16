
package ua.gradsoft.jungle.persistence.ejbqlao.util;

import java.util.Collection;
import java.util.List;

/**
 *Utilities, for generating ejb/ql (for using from criteria-heloers)
 * @author rssh
 */
public class QLGeneratorUtils {

    /**
     * write to string builder collection of strings, separated by <code> sep </code>
     * @param sb - StringBuilder, where output.
     * @param parts - parts to concatenate
     * @param sep - separator, which will be inserted between parts.
     */
    public static void implode(StringBuilder sb, Collection<String> parts, String sep)
    {
      boolean isFirst=true;  
      for(String part :parts) {
          if (!isFirst) {
              sb.append(sep);
          }else{
              isFirst=false;
          }
          sb.append(part);
      }  
    }

    public static String implode(Collection<String> parts, String sep)
    {
       StringBuilder sb = new StringBuilder();
       implode(sb,parts,sep);
       return sb.toString();
    }

    /**
     * output all conditions (in brackets) separated by ' and '.
     * id.e. (c1) and (c2) and ...... and (cn).
     * @param sb - where to output.
     * @param conditions - conditions to output.
     */
    public static void andWhere(StringBuilder sb, Collection<String> conditions)
    {
        boolean isFirst=true;
        for(String condition: conditions) {
            if (!isFirst) {
                sb.append(" and ");
            }else{
                isFirst=false;
            }
            sb.append("(").append(condition).append(")");
        }
    }


    public static String andWhere(Collection<String> conditions)
    {
      StringBuilder sb = new StringBuilder();
      andWhere(sb,conditions);
      return sb.toString();
    }

    public static String generateEjbQl(List<String> selects,
                                       List<String> fromParts,
                                       List<String> whereParts,
                                       List<String> orderByParts)
    {
      StringBuilder sb = new StringBuilder();
      sb.append("select ");
      implode(sb, selects, ", ");
      sb.append(" from ");
      implode(sb, fromParts, ", ");
      if (whereParts!=null && !whereParts.isEmpty()) {
          sb.append(" where ");
          andWhere(sb,whereParts);
      }
      if (orderByParts!=null && !orderByParts.isEmpty()) {
          sb.append(" order by ");
          implode(sb, orderByParts, ", ");
      }
      return sb.toString();
    }

}
