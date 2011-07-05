
package ua.gradsoft.jungle.localization.testdata;

import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import ua.gradsoft.jungle.localization.WithTranslations;

/**
 *Test entity for message
 * @author rssh
 */
@Entity
@WithTranslations
public class Message implements Serializable
{

    @Id
    public int getId() {
        return id_;
    }

    public void setId(int id) {
        this.id_ = id;
    }

    @WithTranslations
    @Column(name="message", length=255)
    public String getMessage_() {
        return message_;
    }

    public void setMessage_(String message_) {
        this.message_ = message_;
    }



    private int id_;
    private String message_;

}
