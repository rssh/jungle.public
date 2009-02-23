
package org.apache.ivy.plugins.resolver.build;

import java.io.File;
import junit.framework.TestCase;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.BuildLogger;
import org.apache.tools.ant.DefaultLogger;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.ProjectHelper;

/**
 *Ant test
 * @author rssh
 */
public class BuildResolverAntTest extends TestCase
{



  public void testStructure1WithAnt() throws Exception
  {
      doAntTarget("test/repositories/build/structure1/p2","test.all");
  }


  private void doAntTarget(String dirname, String target) throws Exception
  {
      Project project = new Project();
      File dir = new File(dirname);
      File antFile = new File(dirname,"build.xml");
      project.init();
      //project.setUserProperty(dirname, target)
      project.setUserProperty("ant.file", antFile.getAbsolutePath());
      project.setBasedir(dir.getAbsolutePath());
      project.setUserProperty("basedir", dir.getAbsolutePath());
      ProjectHelper.configureProject(project,antFile);
      project.addDataTypeDefinition("build", org.apache.ivy.plugins.resolver.build.BuildResolver.class);


    // Configure logging verbosity
        BuildLogger logger = new DefaultLogger();
        logger.setMessageOutputLevel(Project.MSG_VERBOSE);
        logger.setOutputPrintStream(System.out);
        logger.setErrorPrintStream(System.err);
        project.addBuildListener(logger);
      try {
        project.executeTarget(target);
      }catch(BuildException ex){
          ex.printStackTrace();
          throw ex;
      }

  }


}
