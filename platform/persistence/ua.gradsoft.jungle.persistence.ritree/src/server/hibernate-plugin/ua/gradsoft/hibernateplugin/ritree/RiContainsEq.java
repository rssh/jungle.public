package ua.gradsoft.hibernateplugin.ritree;

import java.io.Serializable;
import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import org.hibernate.annotations.FilterDef;
import org.hibernate.annotations.ParamDef;
import ua.gradsoft.jungle.persistence.ritree.RiInterval;

/**
 *Pseudo-entity foe contains and equ relationship.
 * @author rssh
 */
@javax.persistence.Entity
@org.hibernate.annotations.Entity(mutable=false)
@org.hibernate.annotations.Persister(impl=RiContainsEqPersister.class)
@javax.persistence.Table(schema="ri_tree", name="ri_time_intervals")
@FilterDef(name="ri",
   parameters={@ParamDef(name="bottom", type="long"),
               @ParamDef(name="top", type="long")
})
public class RiContainsEq  implements Serializable, RiFakeEntity
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


    public RiContains createEmpty()
    {
      return new RiContains();
    }

    private RiInterval interval_;

    
}
