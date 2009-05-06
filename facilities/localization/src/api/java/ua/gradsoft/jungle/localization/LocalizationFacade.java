package ua.gradsoft.jungle.localization;

import java.util.Collection;
import java.util.List;

/**
 * Facade for localization bundle API
 */
public interface LocalizationFacade
{

   /**
    * get default language for localization bundel
    * @param bundle name of localization bundle.
    * @return 2-letter ISO 639-1 language code. (uppercase)
    */
   public String  getDefaultLanguageForBundle(String bundle);

   /**
    * get list of supported languages for bundle
    * @param bundle -- name of localization bundle.
    * @return list of 2-letter ISO 639-1 language code.
    */
   public List<String>  getSupportedLanguagesForBundle(String bundle);


   /**
    * @param countryCode - 2-letter iso 3166 country code (uppercase)
    * @return true if exists oficial language for this country, otherwise false.
    */
   public boolean  hasDefaultLanguageForCountry(String countryCode);


   /**
    * @param countryCode  2-letter iso 3166 country code (uppercase)
    * @return default ifficial language for this country, if one exists, otherwise
    *  - throws IllegalArgumemtExceptiom
    */
   public String getDefaultLanguageForCountry(String countryCode);

   /**
    * get list of languages, which used on territory of country.
    * @param countryCode 2-letter iso 3166 country code (uppercase)
    * @return list of 2-letter iso 639-1 language codes (uppercase)
    */
   public List<String>  getUsedLanguageIdsForCountry(String countryCode);


   /**
    * translate set of fields from objects to target language.
    * If entity of such class is defined in bundle, then
    * we translate fields with names, as in <code> fieldNames</code>
    * for entity with id <code> id </code>.
    * @param language - 2-letter iso369-1 language code, for la
    * @param entityClassname - classname of entity, as described in bundle.
    * @param id - entity if
    * @param fieldNames - list of fields to translate.
    * @return list of translated fields, if such entity exists and fields
    * can be translated, otherwise exception.
    */
   public List<String>   translateFieldsById(String language,
                                             String entityClassname,
                                             Object id,
                                             List<String> fieldNames);

   /**
    * By list of of entity id's receive translation of fields from entities with
    * such ids.
    * @param language - 2-letter iso 639-1 language code (uppercase).
    * @param ids - list of id's
    * @param fieldNames - list of fields to translate.
    * @return list of list of translated fields, if such entity exists and fields
    * can be translated, otherwise throw exception. (Usially IleagaAccessException)
    */
   public List<List<String>> translateFieldsByIds(String language,
                                                  String entityClassname,
                                                  List<Object> ids,
                                                  List<String> fieldNames);

   /**
    * translate bean to given language. Can recursive process subbeans.
    * @param <T>  bean type
    * @param bean to translate
    * @param languageCode 2-letter iso 639-1 language code (uppercase).
    * @return translated bean (as specified in bundle) ot throw RuntimeException
    *  if something is incorrect.
    */
   public<T>  T  translateBean(T bean, String languageCode);

   /**
    * translate collections of bean to given language. Can recursive process subbeans.
    * @param <T>  bean type
    * @param beans to translate. Note, that type of beans must be the same in all collection.
    * @param languageCode 2-letter iso 639-1 language code (uppercase).
    * @return translated beans (as specified in bundle) ot throw RuntimeException
    *  if something is incorrect.
    */
   public<T> Collection<T>  translateBeans(Collection<T> beans, String languageCode);

}
