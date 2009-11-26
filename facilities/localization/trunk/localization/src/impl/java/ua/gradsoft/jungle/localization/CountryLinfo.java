
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
import javax.persistence.Table;

/**
 *POJO class for country_linfo
 * @author rssh
 */
@Entity
@Table(name="countries_linfos")
public class CountryLinfo implements Serializable
{

    /**
     * ISO 31166 Country code.
     */
    @Id
    @Column(name="CODE", length=CODE_LENGTH, columnDefinition="char(2) primary key")
    public String getCode()
    { return code_; }
    
    public void setCode(String code)
    {
     code_=code;   
    }
    
    @Column(name="name_eng", length=NAME_ENG_LENGTH, nullable=false)
    public String getNameEng()
    {
      return nameEng_;  
    }
    
    public void setNameEng(String nameEng)
    {
        nameEng_=nameEng;
    }
    
    @Column(name="default_language_code", length=LanguageInfo.CODE_LENGTH,
            columnDefinition="char(2)", nullable=true)
    public String getDefaultLanguageCode()
    {
      return defaultLanguageCode_;  
    }
    
    public void setDefaultLanguageCode(String defaultLanguageCode)
    {
        defaultLanguageCode_=defaultLanguageCode;
    }

    /**     
     * @return list of languages, used in this country
     */
    @ManyToMany(fetch=FetchType.LAZY)
    @JoinTable(name="languages_in_countries",
       joinColumns=@JoinColumn(name="language_code", referencedColumnName="code"),
       inverseJoinColumns=@JoinColumn(name="country_code", 
                                                     referencedColumnName="code")
    )
    public List<LanguageInfo> getUsedLanguages()
    {
      return usedLanguages_;
    }

    public void setUsedLanguages(List<LanguageInfo> usedLanguages)
    {
       usedLanguages_=usedLanguages;
    }
    
    private String code_;
    private String nameEng_;
    private String defaultLanguageCode_;

    private List<LanguageInfo> usedLanguages_;

    public static final int CODE_LENGTH=2;
    public static final int NAME_ENG_LENGTH=64;

}
