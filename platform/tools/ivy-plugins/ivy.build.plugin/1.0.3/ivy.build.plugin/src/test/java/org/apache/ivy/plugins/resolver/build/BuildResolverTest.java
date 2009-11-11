package org.apache.ivy.plugins.resolver.build;

import java.io.File;
import junit.framework.TestCase;
import org.apache.ivy.core.event.EventManager;
import org.apache.ivy.core.module.descriptor.Artifact;
import org.apache.ivy.core.module.descriptor.DefaultArtifact;
import org.apache.ivy.core.module.descriptor.DefaultDependencyDescriptor;
import org.apache.ivy.core.module.id.ModuleRevisionId;
import org.apache.ivy.core.report.ArtifactDownloadReport;
import org.apache.ivy.core.report.DownloadReport;
import org.apache.ivy.core.resolve.DownloadOptions;
import org.apache.ivy.core.resolve.ResolveData;
import org.apache.ivy.core.resolve.ResolveEngine;
import org.apache.ivy.core.resolve.ResolveOptions;
import org.apache.ivy.core.resolve.ResolvedModuleRevision;
import org.apache.ivy.core.settings.IvySettings;
import org.apache.ivy.core.sort.SortEngine;
import org.apache.ivy.plugins.resolver.FileSystemResolver;
import org.apache.ivy.util.DefaultMessageLogger;
import org.apache.ivy.util.FileUtil;
import org.apache.ivy.util.Message;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.taskdefs.Copy;
import org.apache.tools.ant.types.FileSet;

public class BuildResolverTest extends TestCase
{

  private IvySettings _settings;
  private ResolveEngine _engine;
  private ResolveData _data;
  private File _cache;

  private File _workdir;
  private File _builddir;
  private File _repodir;
  private File _p1dir;


  protected void setUp() throws Exception
  {
      _settings=new IvySettings();
      Message.setDefaultLogger(new DefaultMessageLogger(99));
      _engine=new ResolveEngine(_settings, new EventManager(), new SortEngine(_settings));
      _cache=new File("build/cache");

      _data=new ResolveData(_engine, new ResolveOptions());
      _cache.mkdirs();
      _settings.setDefaultCache(_cache);

      //create workspace
      _workdir=new File("build/test/BuildResolverTest");
      FileUtil.forceDelete(_workdir);
      _builddir = new File(_workdir,"build");
      _repodir = new File(_workdir,"repo");
      _p1dir = new File(_workdir,"p1");

      //copy p1 to builddir
      Copy copy = new Copy();
      FileSet fileset = new FileSet();
      fileset.setDir(new File("test/repositories/build/buildexample"));
      copy.addFileset(fileset);
      copy.setTodir(_p1dir);
      copy.setProject(new Project());
      copy.execute();

  }

  protected void tearDown() throws Exception
  {
      FileUtil.forceDelete(_cache);
      FileUtil.forceDelete(_workdir);
  }

  public void testBuildCallsP1() throws Exception
  {
      BuildResolver buildResolver=new BuildResolver();
      buildResolver.setSettings(_settings);
      FileSystemResolver repoResolver=new FileSystemResolver();
      repoResolver.setSettings(_settings);
      repoResolver.addIvyPattern(_repodir.getAbsolutePath()+"/[organization]/[module]/ivys/ivy-[revision].xml");
      repoResolver.addArtifactPattern(_repodir.getAbsolutePath()+"/[organization]/[module]/[type]s/[artifact]-[revision].[ext]");
      repoResolver.setName("local");
      buildResolver.setName("build.local");
      buildResolver.setBuildResolverDir(_repodir.getAbsolutePath());
      buildResolver.add(repoResolver);
      BuildDescriptionEntry bde = new BuildDescriptionEntry();
      bde.setOrganization("org.apache.ivy");
      bde.setName("buildexample");
      bde.setPublishTarget("publish.local");
      bde.setBuildDirectory(_p1dir.getAbsolutePath());
      buildResolver.addConfiguredDependencyBuild(bde);

      ModuleRevisionId mrid = ModuleRevisionId.newInstance(
                                 "org.apache.ivy", "buildexample","1.0");
      ResolvedModuleRevision rmr = buildResolver.getDependency(new DefaultDependencyDescriptor(mrid,false), _data);
      assertNotNull(rmr);
      
      Artifact artifact = new DefaultArtifact(mrid,rmr.getPublicationDate(),"buildexample","txt","txt");
      Artifact[] artifacts = new Artifact[1];
      artifacts[0]=artifact;
      DownloadReport report = buildResolver.download(artifacts, new DownloadOptions());
      assertNotNull(report);
      
      assertEquals(1,report.getArtifactsReports().length);
      
      ArtifactDownloadReport ar = report.getArtifactReport(artifact);
      assertNotNull(ar);
      
      // at least, check that file in ivy.build.resolver.dir exists
      
      File orgf = new File(_repodir,"org.apache.ivy" );
      assertTrue(orgf.exists());
      
      File modf = new File(orgf,"buildexample" );
      assertTrue(modf.exists());

      File typef = new File(modf,"txts");
      assertTrue(typef.exists());
      
      File artf = new File(typef, "buildexample-1.0.txt");
      assertTrue(artf.exists());

  }


}
