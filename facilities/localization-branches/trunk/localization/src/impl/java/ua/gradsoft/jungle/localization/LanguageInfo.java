package ua.gradsoft.jungle.localization;

import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 *LanguageInfo - POJO object for information about language.
 * @author rssh
 */
@Entity
@Table(name="languages")
public class LanguageInfo implements Serializable
{
    
    /**    
     * @return two-letter iso 639-1 language code (uppercase)
     */
    @Id
    @Column(name="code",length=CODE_LENGTH)
    public String getCode()
    {
      return code_;  
    }

    public void setCode(String code)
    {
      code_=code;
    }

    /**
     * get three-letter terminologival iso 639-2 language code.
     *(uppercase)
     */
    @Column(name="code_iso_639_2", length=ISO639_2_CODE_LENGTH, unique=true)
    public String getCodeIso639_2()
    {
      return codeIso639_2_;
    }

    public void setCodeIso639_2(String codeIso639_2)
    {
      codeIso639_2_=codeIso639_2;  
    }


    /**
     * get name of language (by default - in english).
     */
    @Column(name="name_eng", length=NAME_LENGTH, unique=true)
    public String getName()
    {
      return name_;
    }

    public void setName(String name)
    {
      name_=name;  
    }


    private String code_;
    private String codeIso639_2_;
    private String name_;

    public static final int CODE_LENGTH=2;
    public static final int ISO639_2_CODE_LENGTH=3;
    public static final int NAME_LENGTH=64;
}
