package ua.gradsoft.phpjao;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;


public class Main
{
 public static void main(String[] args)
 {
   try {
     Facade facade = new Facade();
     Configuration cfg = parseArgs(args);
     facade.configure(cfg);
     facade.process();
   }catch(PhpJaoConfigException ex){
       ex.printStackTrace();
   }catch(PhpJaoProcessingException ex){
       ex.printStackTrace();
   }
 }

 static class CMDConfiguration implements Configuration
 {

        public List<String> getClassnames() {
            return classNames_;
        }

        public List<String> getIncludeDirs() {
            return includeDirs_;
        }

        public List<String> getIncludeJars() {
            return includeJars_;
        }

        public String getOutputFile() {
            return outputFile_;
        }

        public String getPHPJAOHome() {
            return phpJaoHome_;
        }

        public String getPhpHeader()
        { return phpHeader_; }

        public boolean getWithoutRequireHeader()
        { return withoutRequireHeader_; }

     CMDConfiguration(String[] args) throws PhpJaoConfigException
     {
         for (int i=0; i<args.length; ++i) {
            if (args[i].equals("--classes")) {
                String sClasses = args[i+1];
                String[] arrClasses = sClasses.split(";");
                classNames_ = Arrays.asList(arrClasses);
                ++i;
            }else if (args[i].equals("--includes")) {
                String sIncludes = args[i+1];
                String[] arrIncludes = sIncludes.split(";");
                includeDirs_ = Arrays.asList(arrIncludes);
                ++i;
            }else if (args[i].equals("--jars")) {
                String sJars = args[i+1];
                String[] arrJars = sJars.split(";");
                includeJars_ = Arrays.asList(arrJars);
                ++i;
            }else if (args[i].equals("--outputFile")){
                outputFile_ = args[i+1];
                ++i;
            }else if (args[i].equals("--phpHeader")){
                phpHeader_ = args[i+1];
                ++i;
            }else if (args[i].equals("--withoutRequireHeader")) {
                withoutRequireHeader_=true;
            }else{
                throw new PhpJaoConfigException("Unknown option:"+args[i]);
            }
         }
     }

     private List<String> classNames_ = Collections.emptyList();
     private List<String> includeDirs_ = Collections.emptyList();
     private List<String> includeJars_ = Collections.emptyList();
     private String outputFile_;
     private String phpJaoHome_;
     private String phpHeader_ = "PHPJAO.php";
     private boolean withoutRequireHeader_=false;


 }

 private static Configuration parseArgs(String[] args) throws PhpJaoConfigException
 {
     return new CMDConfiguration(args);
 }


}
