package ua.gradsoft.jungle.localization.testdata;

import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import ua.gradsoft.jungle.localization.WithTranslations;

/**
 *Test Cirty class
 * @author rssh
 */
@Entity
@Inheritance(strategy=InheritanceType.JOINED)
@WithTranslations
public class City implements Serializable
{

    @Id
    public int getId() {
        return id_;
    }

    public void setId(int id) {
        this.id_ = id;
    }

    @WithTranslations
    @Column(name="name", length=20)
    public String getName() {
        return name_;
    }

    public void setName(String name) {
        this.name_ = name;
    }


    @WithTranslations
    @Column(name="description", length=255)
    public String getDescription() {
        return description_;
    }

    public void setDescription(String description) {
        this.description_ = description;
    }

    @WithTranslations
    @ManyToOne
    @JoinColumn(name="country_code")
    public Country getCountry() {
        return country_;
    }

    public void setCountry(Country country)
    {
        country_=country;
    }



    private int id_;
    private String name_;
    private String description_;
    private Country country_;

}
