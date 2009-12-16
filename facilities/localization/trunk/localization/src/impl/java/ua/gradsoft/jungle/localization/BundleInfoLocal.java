
package ua.gradsoft.jungle.localization;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 *The same object, as bundle info, but local (without hibernate proxies)
 * @author rssh
 */
class BundleInfoLocal implements Serializable
{

    public String getName()
    { return name_; }

    public void setName(String name)
    { name_=name; }

    public LanguageInfo getPrimaryLanguage()
    {
      return primaryLanguage_;
    }

    public void setPrimaryLanguage(LanguageInfo primaryLanguage)
    {
      primaryLanguage_=primaryLanguage;
    }


    public List<LanguageInfo> getSupportedLanguages()
    {
        return supportedLanguages_;
    }

    public void setSupportedLanguages(List<LanguageInfo> supportedLanguages)
    { supportedLanguages_=supportedLanguages; }

    public BundleInfoLocal(BundleInfo bi)
    {
       name_ = bi.getName();
       primaryLanguage_ = bi.getPrimaryLanguage();
       supportedLanguages_ = new ArrayList<LanguageInfo>(bi.getSupportedLanguages().size());
       supportedLanguages_.addAll(bi.getSupportedLanguages());
    }

    private String name_;
    private LanguageInfo  primaryLanguage_;
    private List<LanguageInfo>  supportedLanguages_;


}
