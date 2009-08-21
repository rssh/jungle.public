
package ua.gradsoft.jungle.localization;

import java.io.Serializable;
import java.util.List;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

/**
 *POJO for localization bundle
 * @author rssh
 */
@Entity
@Table(name="localization_bundles")
public class BundleInfo implements Serializable
{

    @Id
    @Column(name="name", length=NAME_LENGTH)
    public String getName()
    { return name_; }

    public void setName(String name)
    { name_=name; }

    @ManyToOne
    @JoinColumn(name="primary_language", referencedColumnName="code", nullable=false)
    public LanguageInfo getPrimaryLanguage()
    {
      return primaryLanguage_;
    }

    public void setPrimaryLanguage(LanguageInfo primaryLanguage)
    {
      primaryLanguage_=primaryLanguage;
    }


    @ManyToMany(fetch=FetchType.LAZY)
    @JoinTable(name="localization_bundle_languages",
      joinColumns=@JoinColumn(name="bundle_name"/*, referencedColumnName="bundle_name"*/),
      inverseJoinColumns=@JoinColumn(name="language_code" /*, referencedColumnName="language_code"*/)
    )
    public List<LanguageInfo> getSupportedLanguages()
    {
        return supportedLanguages_;
    }


    public void setSupportedLanguages(List<LanguageInfo> supportedLanguages)
    { supportedLanguages_=supportedLanguages; }

    private String name_;
    private LanguageInfo  primaryLanguage_;
    private List<LanguageInfo>  supportedLanguages_;

    public static final int NAME_LENGTH = 64;
}
