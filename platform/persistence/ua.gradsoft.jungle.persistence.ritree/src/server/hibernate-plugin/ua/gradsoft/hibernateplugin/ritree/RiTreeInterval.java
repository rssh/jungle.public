package ua.gradsoft.hibernateplugin.ritree;

import java.io.Serializable;
import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import org.hibernate.annotations.ResultCheckStyle;
import ua.gradsoft.jungle.persistence.ritree.RiInterval;

/**
 *Entity for RiTreeInterval
 * @author rssh
 */
@javax.persistence.Entity
@org.hibernate.annotations.Entity(mutable=false)
@org.hibernate.annotations.Immutable
@javax.persistence.Table(schema="ri_tree", name="ri_time_intervals")
@org.hibernate.annotations.SQLInsert(callable=true,sql="{call ri_tree.insert_interval(?,?)}")
@org.hibernate.annotations.SQLDelete(callable=true,sql="{call ri_tree.delete_interval(?,?)}",
                                     check=ResultCheckStyle.NONE)
public class RiTreeInterval implements Serializable
{

    @EmbeddedId
    @AttributeOverrides({
        @AttributeOverride(name="begin", column=@Column(name="lower")),
        @AttributeOverride(name="end", column=@Column(name="upper"))
    })
    RiInterval getPk()
    { return pk_; }

    public void setPk(RiInterval pk)
    {
      pk_=pk;
    }

    private RiInterval pk_;
}
