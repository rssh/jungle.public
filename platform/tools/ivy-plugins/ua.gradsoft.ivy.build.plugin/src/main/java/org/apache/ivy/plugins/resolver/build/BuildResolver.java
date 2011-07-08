package org.apache.ivy.plugins.resolver.build;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Iterator;
import org.apache.ivy.core.module.descriptor.Artifact;
import org.apache.ivy.core.module.descriptor.DependencyDescriptor;
import org.apache.ivy.core.module.id.ModuleRevisionId;
import org.apache.ivy.core.report.ArtifactDownloadReport;
import org.apache.ivy.core.report.DownloadReport;
import org.apache.ivy.core.report.DownloadStatus;
import org.apache.ivy.core.resolve.DownloadOptions;
import org.apache.ivy.core.resolve.IvyNode;
import org.apache.ivy.core.resolve.ResolveData;
import org.apache.ivy.core.resolve.ResolvedModuleRevision;
import org.apache.ivy.plugins.parser.ModuleDescriptorParser;
import org.apache.ivy.plugins.parser.ModuleDescriptorParserRegistry;
import org.apache.ivy.plugins.resolver.AbstractResolver;
import org.apache.ivy.plugins.resolver.DependencyResolver;
import org.apache.ivy.plugins.resolver.ResolverSettings;
import org.apache.ivy.plugins.resolver.util.ResolvedResource;
import org.apache.ivy.util.Message;
import org.apache.ivy.util.StringUtils;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.BuildLogger;
import org.apache.tools.ant.DefaultLogger;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.ProjectHelper;


public class BuildResolver extends AbstractResolver
{


    public DownloadReport download(Artifact[] artifacts, DownloadOptions options) {
        DownloadReport firstReport = backResolver.download(artifacts, options);
        List/*<Artifact>*/ nextDownloads = new ArrayList/*<Artifact>*/();
        ArtifactDownloadReport reports[] = firstReport.getArtifactsReports();
        for(int i=0; i<reports.length; ++i) {
            ArtifactDownloadReport r= reports[i];
            if (r.getDownloadStatus()!=DownloadStatus.SUCCESSFUL ) {
                nextDownloads.add(r.getArtifact());
                Message.verbose(getName()+"  "+r.getName()+" can't be downloaded+("+r.getDownloadStatus()+"), will try build");
            }
        }
        int nBuilds = doBuildAndPublishForArtifacts(nextDownloads);
        if (nBuilds==0) {
            return firstReport;
        }else{          
            return backResolver.download(artifacts, options);
        }
    }

    public ResolvedResource findIvyFileRef(DependencyDescriptor dd, ResolveData resolveData) {
        if (backResolver==null) {
            String msg = getName()+": back resolver is not set ";
            Message.error(msg);
            throw new IllegalStateException(msg);
        }
        ResolvedResource retval = backResolver.findIvyFileRef(dd, resolveData);
        Message.verbose(getName()+" search for ivy file for "+dd.getDependencyId().getName());
        if (retval==null) {
            String resourceName = dd.getDependencyId().getName();
            String organization = dd.getDependencyId().getOrganisation();
            BuildDescriptionEntry e = buildDescription.findBuildDescription(organization, resourceName);
            if (e!=null) {
                  if (checkAntFileExists(e)) {
                    if (!e.isBuildWasCalled()) {
                      doBuildAndPublish(e,backResolver.getName());
                    }
                    retval = backResolver.findIvyFileRef(dd, resolveData);
                  }else{
                    Message.verbose(getName()+": and file "+e.getBuildDirectory()+"/"+e.getAntFile()+" does not exists");
                  }                
            }
        }
        return retval;
    }

    public ResolvedModuleRevision getDependency(DependencyDescriptor dd, ResolveData data)
            throws ParseException
    {
        ResolvedModuleRevision firstResolved =  data.getCurrentResolvedModuleRevision();
        ResolvedModuleRevision currentResolved = firstResolved;
        if (currentResolved==null) {
            Message.verbose(getName()+": checking cache for:"+dd);
            currentResolved = findModuleInCache(dd,data,true);
            if (currentResolved!=null) {
                Message.verbose(getName()+": module revision found in cache:"+currentResolved.getId());
            }
        }

        ResolvedModuleRevision svResolved = currentResolved;
        data.setCurrentResolvedModuleRevision(svResolved);
        ResolvedModuleRevision backResolved = null;
        Exception backResolverException = null;
        try {
          backResolved = backResolver.getDependency(dd, data);
        }catch(Exception ex){
            Message.verbose("problem occured why resolving "+dd+
                            " with resolver "+backResolver+": "+StringUtils.getStackTrace(ex));
            backResolverException=ex;
        }

        currentResolved = mergeResolvedModuleRevisions(backResolved,svResolved,dd,data);

        ResolvedResource ivyRef = findIvyFileRef(dd,data);
        checkInterrupted();
        ResolvedModuleRevision buildResolved=null;

        if (ivyRef!=null) {
            // try again back resolver: may be situation was changed.

            ModuleRevisionId mrid = dd.getDependencyRevisionId();
            ModuleDescriptorParser parser = ModuleDescriptorParserRegistry.getInstance().getParser(ivyRef.getResource());
            if (parser==null) {
                Message.warn("no module descriptor parser avaible for "+ivyRef.getResource());
            }else{
                // check - is this dependency was resolved.
                if (getSettings().getVersionMatcher().isDynamic(mrid)) {
                    ModuleRevisionId resolvedMrid = ModuleRevisionId.newInstance(mrid, ivyRef.getRevision());
                    IvyNode node = data.getNode(resolvedMrid);
                    if (node!=null && node.getModuleRevision()!=null) {
                        buildResolved = node.getModuleRevision();
                        node.getModuleRevision().getReport().setSearched(true);
                    }
                }
            }

            if (buildResolved!=null) {
              currentResolved = mergeResolvedModuleRevisions(currentResolved,buildResolved,dd,data);
            }

            try {
               backResolved = backResolver.getDependency(dd, data);
            }catch(Exception ex){
               Message.verbose("problem occured why resolving "+dd+
                            " with resolver "+backResolver+": "+StringUtils.getStackTrace(ex));
               backResolverException=ex;
            }
            currentResolved = mergeResolvedModuleRevisions(currentResolved,backResolved,dd,data);
        }

        if (currentResolved==null && backResolverException != null) {
            throw new RuntimeException("exception in back resolver",backResolverException);
        }

        return currentResolved;
    }

    public ResolvedModuleRevision mergeResolvedModuleRevisions(ResolvedModuleRevision x, ResolvedModuleRevision y, DependencyDescriptor dd, ResolveData data)
    {
      if (x==null) {
          return y;
      }else if (y==null) {
          return x;
      }else{
          if (isAfter(x,y,data.getDate())) {
              return x;
          }else{
              return y;
          }
      }
    }

    public ResolvedModuleRevision  resolvedRevision(ResolvedModuleRevision mr)
    {
        if (!resolveFirst && mr!=null) {
            return new ResolvedModuleRevision(mr.getResolver(),this, mr.getDescriptor(), mr.getReport(), mr.isForce());           
        }else{
            return mr;
        }
    }


    public void publish(Artifact arg0, File arg1, boolean arg2) throws IOException {
        throw new UnsupportedOperationException("publich to build resolver is not supported");
    }

    private int doBuildAndPublishForArtifacts(List/*<Artifact>*/ artifacts)
    {
        Message.verbose(getName()+": doBuildAndPublishForArtifacts begin");
        int nBuilds=0;
        Iterator it = artifacts.iterator();
        while(it.hasNext()) {            
            Artifact artifact = (Artifact)it.next();            
            BuildDescriptionEntry e = buildDescription.findBuildDescription(artifact.getModuleRevisionId().getOrganisation(),
                                                artifact.getModuleRevisionId().getName());
            if (e!=null) {              
              if (checkAntFileExists(e)) {
                if (!e.isBuildWasCalled()) {
                  doBuildAndPublish(e, backResolver.getName());
                  ++nBuilds;
                }
              }
            }
        }

        Message.verbose(getName()+": doBuildAndPublishForArtifacts end, nBuilds="+nBuilds);
        return nBuilds;
    }

    private void doBuildAndPublish(BuildDescriptionEntry e, String resolverName)
    {
       if (e.isBuildWasCalled()) {
           return;
       }

       Project project = new Project();
       File dir = new File(e.getBuildDirectory());
       File antFile = new File(e.getBuildDirectory(),e.getAntFile());
       project.init();
       project.setUserProperty("ant.file" , antFile.getAbsolutePath());
       project.setUserProperty("ivy.build.resolver", resolverName);
       if (ivyBuildResolverDir!=null) {
          project.setUserProperty("ivy.build.resolver.dir", ivyBuildResolverDir);
       }
       ProjectHelper.configureProject(project, antFile);
       project.setBaseDir(dir);

        // Configure logging verbosity
        BuildLogger logger = new DefaultLogger();
        logger.setMessageOutputLevel(this.verbose ? Project.MSG_VERBOSE
                : this.quiet ? Project.MSG_WARN : Project.MSG_INFO);
        logger.setOutputPrintStream(System.out);
        logger.setErrorPrintStream(System.err);
        project.addBuildListener(logger);

        // Execute task
        Message.verbose("performing build resolver build in " + dir);
        try {
            project.executeTarget(e.getPublishTarget());          
        } catch (BuildException ex) {
            ex.printStackTrace(System.out);
            Message.verbose("build resolver build failed: " + e);
            throw new BuildException("exception in depended build",ex);
        }finally{
            e.markCallOfBuild();
        }


    }

    private boolean checkAntFileExists(BuildDescriptionEntry e)
    {   
      File antFile = new File(e.getBuildDirectory(),e.getAntFile());
      return antFile.exists();
    }

   
    public void  add(DependencyResolver dependencyResolver)
    {
       if (backResolver!=null) {
           Message.warn(getName()+":backResolver is not empty, override");
       }
       backResolver=dependencyResolver; 
    }
    
    
    public void  addConfiguredDependencyBuild(BuildDescriptionEntry entry)
    {
      buildDescription.addEntry(entry);  
    }
    
    public void  addConfiguredRootBuildDirectory(RootBuildDirectoryEntry entry)
    {
      buildDescription.addRootBuildDirectoryEntry(entry);  
    }

    public boolean isVerbose()
    { return verbose; }
    
    public void setVerbose(boolean theVerbose)
    { verbose = theVerbose; }
    
    public boolean isQuiet()
    { return quiet; }
    
    public void setQuiet(boolean theQuiet)
    { quiet=theQuiet; }

    public boolean isResolveFirst()
    { return resolveFirst; }

    public void  setResolveFirst(boolean theResolveFirst)
    { resolveFirst=theResolveFirst; }

    public String  getBuildResolverDir()
    { return ivyBuildResolverDir; }
    
    public void  setBuildResolverDir(String theBuildResolverDir)
    {
      ivyBuildResolverDir=theBuildResolverDir;  
    }

  private DependencyResolver backResolver=null;
  private BuildDescription   buildDescription=new BuildDescription();
  private String             ivyBuildResolverDir=null;
  private boolean            verbose=false;
  private boolean            quiet=false;
  private boolean            resolveFirst=true;

  

}
