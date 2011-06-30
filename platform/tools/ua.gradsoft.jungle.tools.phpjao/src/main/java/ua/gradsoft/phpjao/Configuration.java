package ua.gradsoft.phpjao;

import java.util.List;

/**
 *Configuration interface for phpjao
 * @author rssh
 */
public interface Configuration {


    String getPHPJAOHome();

    String getPhpHeader();

    boolean getWithoutRequireHeader();

    List<String>  getIncludeDirs();
    
    List<String>  getIncludeJars();

    List<String>  getClassnames();

    String  getOutputFile();

}
