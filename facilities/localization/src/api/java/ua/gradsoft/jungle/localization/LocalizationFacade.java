package ua.gradsoft.jungle.localization;

import java.math.BigDecimal;
import java.util.List;

public interface LocalizationFacade
{

 public List<String>   translateFieldsById(String bundle, String language,
                                           String entityName, BigDecimal id, 
                                           List<String> fieldNames);

 public List<List<String>> translateFiledsByIds(String bundle, String language,
                                                List<BigDecimal> ids);

 public String   translateMessage(String bundle, String message, 
                                  String language);

 public List<String>   translateMessages(String bundle, 
                                        List<String> messages, 
                                        String language);

 public List<String>  getSupportedLanguages(String bundle);

 public boolean  hasDefaultLanguage(String countryCode);

 public String getDefaultLanguage(String countryCode);

 public List<String>  getUsedLanguageIds(String countryCode);


}
