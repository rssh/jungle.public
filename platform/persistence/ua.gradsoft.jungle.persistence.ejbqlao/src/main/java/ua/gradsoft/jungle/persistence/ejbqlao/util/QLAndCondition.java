
package ua.gradsoft.jungle.persistence.ejbqlao.util;

import java.util.List;

/**
 *Condition which
 * @author rssh
 */
public class QLAndCondition extends QLCondition
{

    public QLAndCondition(List<QLCondition> components) {
        this.components = components;
    }

    public void add(QLCondition other)
    {
      if (other instanceof QLAndCondition) {
          QLAndCondition andOthers = (QLAndCondition)other;
          components.addAll(andOthers.getComponents());
      }
    }

    @Override
    public void outql(StringBuilder sb) {
        if (components.isEmpty()) {
            sb.append("(1=1)");
        }else{
            sb.append("(");
            boolean isFirst=true;
            for(QLCondition c: components) {
                if (!isFirst) {
                    sb.append(") and (");
                }
                c.outql(sb);
            }
            sb.append(")");
        }
    }

    public List<QLCondition> getComponents() {
        return components;
    }

    public void setComponents(List<QLCondition> components) {
        this.components = components;
    }




    private List<QLCondition> components;
}
