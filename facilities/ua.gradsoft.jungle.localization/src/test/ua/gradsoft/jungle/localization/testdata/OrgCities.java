package ua.gradsoft.jungle.localization.testdata;

import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import ua.gradsoft.jungle.localization.WithTranslations;

/**
 *
 * @author rssh
 */
@Entity
@WithTranslations
public class OrgCities implements Serializable {

    @Id
    public int getId() {
        return id_;
    }

    public void setId(int id) {
        this.id_ = id;
    }

    @WithTranslations
    @ManyToOne
    @JoinColumn(name="location_city")
    public City getLocationCity() {
        return locationCity_;
    }

    @WithTranslations
    public void setLocationCity(City locationCity) {
        this.locationCity_ = locationCity;
    }

    @WithTranslations
    @Column(name="name")
    public String getName() {
        return name_;
    }

    public void setName(String name) {
        this.name_ = name;
    }

    @WithTranslations
    @ManyToOne
    @JoinColumn(name="residence_city")
    public City getResidenceCity() {
        return residenceCity_;
    }

    public void setResidenceCity(City residenceCity) {
        this.residenceCity_ = residenceCity;
    }


    private int id_;
    private String name_;
    private City locationCity_;
    private City residenceCity_;

}
