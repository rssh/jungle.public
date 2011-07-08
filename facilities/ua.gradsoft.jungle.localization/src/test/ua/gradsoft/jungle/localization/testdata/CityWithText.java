
package ua.gradsoft.jungle.localization.testdata;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import ua.gradsoft.jungle.localization.WithTranslations;

/**
 *City, extended by text
 * @author rssh
 */
@Entity
@WithTranslations
public class CityWithText extends City
{

    @Column(name="text", length=1024)
    @WithTranslations
    public String getText()
    { return text_; }

    public void setText(String text)
    { text_=text; }

    private String text_;
}
