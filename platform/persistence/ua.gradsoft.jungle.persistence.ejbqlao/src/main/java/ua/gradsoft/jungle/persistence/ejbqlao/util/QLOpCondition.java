
package ua.gradsoft.jungle.persistence.ejbqlao.util;

import java.util.List;

/**
 *Condition, which based on lists of other conditions (and, or, etc)
 * @author rssh
 */
public abstract class QLOpCondition extends QLCondition
{
    
    public abstract String op();
    
    public abstract String emptyValue();


    public QLOpCondition(List<QLCondition> components) {
        this.components = components;
    }

    public void add(QLCondition other)
    {
      if (other instanceof QLOpCondition) {
          QLOpCondition andOthers = (QLOpCondition)other;
          if (andOthers.op().equals(this.op())) {
            components.addAll(andOthers.getComponents());
          } else {
            components.add(other);
          }
      }else{
          components.add(other);
      }
    }

    public  void add(String scondition)
    {
        add(new QLRawCondition(scondition));
    }


    @Override
    public void outql(StringBuilder sb) {
        if (components.isEmpty()) {
            sb.append(emptyValue());
        }else{
            sb.append("(");
            boolean isFirst=true;
            for(QLCondition c: components) {
                if (!isFirst) {
                    sb.append(") ").append(op()).append(" (");
                } else {
                    isFirst=false;
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

    protected List<QLCondition> components;



}
