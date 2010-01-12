
package ua.gradsoft.jungle.localization;

/**
 *Class for static utility functions
 * @author rssh
 */
public class LocalizationUtils {

    public static String normalizeLanguage(String language)
    {
      if (language.length()!=2) {
          throw new IllegalArgumentException("invalid language code: must be 2-letter");
      }
      if (Character.isUpperCase(language.charAt(0)) && Character.isLowerCase(language.charAt(1))) {
          return language;
      }else{
          return ""+Character.toUpperCase(language.charAt(0))+Character.toLowerCase(language.charAt(1));
      }
    }


}
