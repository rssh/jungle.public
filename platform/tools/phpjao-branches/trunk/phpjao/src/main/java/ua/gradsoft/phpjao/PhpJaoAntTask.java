package ua.gradsoft.phpjao;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Task;
import org.apache.tools.ant.types.DirSet;
import org.apache.tools.ant.types.Path;

/**
 *Ant task for phpjao
 * @author rssh
 */
public class PhpJaoAntTask  extends Task implements Configuration
{

    public List<String> getClassnames() {
        return classNames_;
    }

    public static class ClassNameEntry
    {
        public String getName()
        { return name_; }

        public void setName(String name)
        { name_=name; }

        private String name_;
    }

    public void addConfiguredClass(ClassNameEntry name)
    { classNames_.add(name.getName()); }

    public List<String> getIncludeDirs() {
        return includeDirs_;
    }

    public void addConfiguredIncludedirs(DirSet dirSet)
    {
        dirSet.setProject(this.getProject());
        includeDirs_.add(dirSet.getDir(getProject()).getAbsolutePath());
    }

    public List<String> getIncludeJars() {
        return includeJars_;
    }

    public void addConfiguredIncludeJars(Path path)
    {
        includeJars_.addAll(Arrays.asList(path.list()));
    }

    public String getOutputFile() {
        return outputFile_;
    }

    public void  setOutputFile(String outputFile)
    {
        outputFile_=outputFile;
    }

    public String getPHPJAOHome() {
        return phpJaoHome_;
    }

    public void  setPHPJAOHome(String home)
    {
        phpJaoHome_=home;
    }

    public String getPhpHeader() {
        return phpHeader_;
    }

    public void  setPhpHeader(String phpHeader)
    {
        phpHeader_=phpHeader;
    }

    public boolean getWithoutRequireHeader()
    {
        return withoutRequireHeader_;
    }

    public void setWithoutRequireHeader(boolean value)
    {   withoutRequireHeader_ = value; }

    @Override
    public void execute() throws BuildException
    {
      try {
        Facade facade = new Facade();
        facade.configure(this);
        facade.process();
      }catch(PhpJaoConfigException ex){
          throw new BuildException(ex.getMessage(),ex);
      }catch(PhpJaoProcessingException ex){
          throw new BuildException(ex.getMessage(),ex);
      }
    }

    private List<String> classNames_ = new ArrayList<String>();
    private List<String> includeDirs_ = new ArrayList<String>();
    private List<String> includeJars_ = new ArrayList<String>();
    private String outputFile_ = null;
    private String phpJaoHome_=null;
    private String phpHeader_="PHPJAO.php";
    private boolean withoutRequireHeader_ = false;


}
