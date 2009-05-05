package ua.gradsoft.jungle.localization;

import java.util.Collection;
import java.util.List;

public interface LocalizationFacade
{


 public List<String>  getSupportedLanguagesForBundle(String bundle);

 public String  getDefaultLanguageForBundle(String bundle);

 public boolean  hasDefaultLanguageForCountry(String countryCode);

 public String getDefaultLanguageForCountry(String countryCode);

 public List<String>  getUsedLanguageIdsForCountry(String countryCode);


 public List<String>   translateFieldsById(String language,
                                           String entityName, Object id,
                                           List<String> fieldNames);

 public List<List<String>> translateFieldsByIds(String bundle,
                                                String language,
                                                List<Object> ids,
                                                List<String> fieldNames);

 public<T>  T  translateBean(T bean, String languageCode);

 public<T> Collection<T>  translateBeans(Collection<T> beans, String languageCode);

}
