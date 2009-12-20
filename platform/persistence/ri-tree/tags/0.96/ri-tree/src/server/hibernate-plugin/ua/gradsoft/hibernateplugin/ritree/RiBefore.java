package ua.gradsoft.hibernateplugin.ritree;

import java.io.Serializable;
import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import org.hibernate.annotations.FilterDef;
import org.hibernate.annotations.Formula;
import org.hibernate.annotations.ParamDef;
import ua.gradsoft.jungle.persistence.ritree.RiInterval;

/**
 *'Formal Entity' for ri_tree_before sql function.
 * Usage: let we have entity MyEntity with embedded field interval,
 * mapped to RiInterval and we want get all entries from
 * general ri_tree tree before some interval [rb_bottom, rb.top],
 * than we use next EJB/SQL sentence:
 *<pre>
 *  select m from MyEntity m,
 *                left join RiBefore rb on m.interval = rb.interval
 *<pre>
 * @author rssh
 */
@javax.persistence.Entity
@org.hibernate.annotations.Entity(mutable=false)
@org.hibernate.annotations.Persister(impl=RiBeforePersister.class)
// point to already existend table.
@javax.persistence.Table(schema="ri_tree", name="ri_time_intervals")
@FilterDef(name="ri",
   parameters={@ParamDef(name="bottom", type="long"),
               @ParamDef(name="top", type="long")
})
public class RiBefore implements Serializable, RiFakeEntity
{


   @EmbeddedId
    @AttributeOverrides({
        @AttributeOverride(name="begin", column=@Column(name="lower")),
        @AttributeOverride(name="end", column=@Column(name="upper"))
    })
    public RiInterval getInterval()
    { return interval_; }

    public void setInterval(RiInterval pk)
    {
      interval_=pk;
    }


    public RiBefore createEmpty()
    {
      return new RiBefore();  
    }

    private RiInterval interval_;

}
