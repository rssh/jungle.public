
package ua.gradsoft.jungle.persistence.ejbqlao.util;

/**
 *
 * @author rssh
 */
public class QLName extends QLPathExpression
{
    
    public QLName(String name)
    { this.name=name; }

    @Override
    public void outql(StringBuilder sb) {
        sb.append(name);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final QLName other = (QLName) obj;
        if ((this.name == null) ? (other.name != null) : !this.name.equals(other.name)) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 41 * hash + (this.name != null ? this.name.hashCode() : 0);
        return hash;
    }



    protected String name;
}
