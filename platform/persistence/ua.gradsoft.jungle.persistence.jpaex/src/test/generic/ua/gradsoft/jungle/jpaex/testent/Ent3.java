package ua.gradsoft.jungle.jpaex.testent;

import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

/**
 *
 * @author rssh
 */
@Entity
public class Ent3 implements Serializable
{

    @Id
    @Column(name="isoCode", length=2, columnDefinition="char(2) primary key")
    public String getIsoCode() { return isoCode_; }
    public void setIsoCode(String isoCode) { isoCode_=isoCode; }

    @Column(name="name", length=20)
    public String getName() {return name_; }
    public void   setName(String name) { name_=name; }

    private String isoCode_;
    private String name_;
}
