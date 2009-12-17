package ua.gradsoft.hibernateplugin.ritree.testdata;

import java.io.Serializable;
import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import ua.gradsoft.jungle.persistence.ritree.RiInterval;

/**
 *MySeries - some data, defined on top of data interval
 * @author rssh
 */
@Entity
@Table(schema="ri_tree_test", name="my_series")
public class MySeries implements Serializable
{

    @Id
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @Column(name="description")
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Embedded
    @AttributeOverrides({
        @AttributeOverride(name="begin",column=@Column(name="interval_begin")),
        @AttributeOverride(name="end",column=@Column(name="interval_end"))
    })
    public RiInterval getInterval() {
        return interval;
    }

    public void setInterval(RiInterval interval) {
        this.interval = interval;
    }

    @Column(name="measurent_value")
    public double getValue() {
        return value;
    }

    public void setValue(double value) {
        this.value = value;
    }


    private int        id;
    private RiInterval interval;
    private String     description;
    private double     value;
}
