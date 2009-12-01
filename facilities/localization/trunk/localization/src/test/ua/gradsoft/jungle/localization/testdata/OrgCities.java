package ua.gradsoft.jungle.localization.testdata;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import ua.gradsoft.jungle.localization.WithTranslations;

/**
 *
 * @author rssh
 */
@Entity
public class OrgCities {

    @Id
    public int getId() {
        return id_;
    }

    public void setId(int id) {
        this.id_ = id;
    }

    @WithTranslations
    @Column(name="location_city")
    public City getLocationCity() {
        return locationCity_;
    }

    @WithTranslations
    public void setLocationCity(City locationCity) {
        this.locationCity_ = locationCity;
    }

    @WithTranslations
    @Column()
    public String getName() {
        return name_;
    }

    public void setName(String name) {
        this.name_ = name;
    }

    @WithTranslations
    @Column(name="residence_city")
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
