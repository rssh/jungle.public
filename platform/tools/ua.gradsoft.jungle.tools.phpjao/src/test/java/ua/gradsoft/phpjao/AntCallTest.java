package ua.gradsoft.phpjao;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import org.apache.tools.ant.BuildException;
import org.junit.Assert;
import org.junit.Test;
;
import org.apache.tools.ant.BuildLogger;
import org.apache.tools.ant.DefaultLogger;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.ProjectHelper;

/**
 *Tests, whcich calls ant scripts in testdata
 * @author rssh
 */
public class AntCallTest {


    @Test
    public void testT1() throws Exception
    {
        doAntCall("testdata/t1","test-generated");
        Assert.assertTrue(checkOutputFile("testdata/t1/phpoutput.log"));
    }

    @Test
    public void testT2_PHP1() throws Exception
    {
        doAntCall("testdata/t2","php1");
        Assert.assertTrue(checkOutputFile("testdata/t2/phpoutput1.log"));
    }

    @Test
    public void testT2_DateTime() throws Exception
    {
        doAntCall("testdata/t2","php2");
        Assert.assertTrue(checkOutputFile("testdata/t2/phpoutput2.log"));
    }

    @Test
    public void testT4() throws Exception
    {
       doAntCall("testdata/t4","test-generated");
       Assert.assertTrue(checkOutputFile("testdata/t4/phpoutput.log"));
    }


    @Test
    public void testT5() throws Exception
    {
       doAntCall("testdata/t5","test-generated");
       Assert.assertTrue(checkOutputFile("testdata/t5/phpoutput.log"));
    }

    @Test
    public void testT6CustomException() throws Exception
    {
       
    }

    @Test
    public void testT7() throws Exception
    {
       doAntCall("testdata/t7","test-generated");
       Assert.assertTrue(checkOutputFile("testdata/t7/phpoutput.log"));
    }

    private boolean checkOutputFile(String fname) throws Exception
    {
        File f = new File(fname);
        BufferedReader r = new BufferedReader(new FileReader(f));
        String s;
        int nErrors=0;
        int nOks=0;
        while((s=r.readLine())!=null) {
           if (s.contains("Error")) {
               ++nErrors;
           }else if (s.contains("OK")) {
               ++nOks;
           }
        }
        return nErrors==0 && nOks!=0;
    }

    private void doAntCall(String dirname, String target) throws Exception
    {
      Project project = new Project();
      File dir = new File(dirname);
      File antFile = new File(dirname, "build.xml");
      project.setUserProperty("ant.file", antFile.getAbsolutePath());
      project.setUserProperty("basedir", dir.getAbsolutePath());
      project.setBaseDir(dir);
      project.init();
      ProjectHelper.configureProject(project, antFile);

      //project.addDataTypeDefinition("phpjao:generate", ua.gradsoft.phpjao.PhpJaoAntTask.class);

      
      BuildLogger logger = new DefaultLogger();
      //logger.setMessageOutputLevel(Project.MSG_VERBOSE);
      logger.setOutputPrintStream(System.out);
      logger.setErrorPrintStream(System.err);
      project.addBuildListener(logger);      

      try {
        project.executeTarget(target);
      }catch(BuildException ex){

          ex.printStackTrace();
          throw new BuildException(ex);
      }
      
    }

}
