package org.apache.ivy.plugins.resolver.build;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Iterator;
import org.apache.ivy.util.Message;


public class BuildDescription
{

  public BuildDescription()
  {
   buildDescriptions = new HashMap/*<String,BuildDescriptionEntry>*/();  
   rootBuildDirectories = new HashMap/*<String,RootBuildDirectoryEntry>*/();
  }

  public void addEntry(BuildDescriptionEntry entry)
  {
    String key = createKey(entry.getOrganization(), entry.getName());
    buildDescriptions.put(key, entry);
  }

  public void addRootBuildDirectoryEntry(RootBuildDirectoryEntry entry)
  {
    rootBuildDirectories.put(entry.getPath(),entry);
  }


  /**
   * return build description or null, if one not found.
   */
  public BuildDescriptionEntry findBuildDescription(String organization, String name)
  {
    String key = createKey(organization,name);
    Object o = buildDescriptions.get(key);
    if (o==null) {
        return findBuildDescriptionFromRoot(organization,name);
    } else {
        return (BuildDescriptionEntry)o;
    }
  }

  private String createKey(String organization, String name)
  {
      return organization+"/"+name;
  }

  private BuildDescriptionEntry findBuildDescriptionFromRoot(
                                           String organization, String name)
  {
    Iterator it = rootBuildDirectories.entrySet().iterator();
    while(it.hasNext()) {
      Map.Entry e = (Map.Entry)it.next();
      RootBuildDirectoryEntry rd = (RootBuildDirectoryEntry)e.getValue(); 
      BuildDescriptionEntry bd = findBuildDescriptionFromRoot(rd,organization,name);
      if (bd!=null) {
        buildDescriptions.put(createKey(organization,name),bd);  
        return bd;
      }
    }
    return null;
  }

  private BuildDescriptionEntry findBuildDescriptionFromRoot(
                                             RootBuildDirectoryEntry rd,
                                             String organization,
                                             String name)
  {
   String buildDir = rd.processBuildDirPattern(organization,name);
   File fpath = new File(rd.getPath());
   if (rd.getCheckSubdirs()) {
    return findBuildDescriptionInSubdirs(rd,fpath,buildDir,organization,name);
   } else {
    return findBuildDescriptionFinal(rd,fpath,buildDir,organization,name);
   }
  } 

  private BuildDescriptionEntry findBuildDescriptionInSubdirs(
                                                  RootBuildDirectoryEntry rd,
                                                  File fpath,
                                                  String buildDir,
                                                  String organization,
                                                  String name) 
    {
    BuildDescriptionEntry bd = findBuildDescriptionFinal(rd,fpath,buildDir,organization, name);
    if (bd!=null) {
       return bd; 
    } else {
       if (!fpath.isDirectory()) return null;
       File[] childs = fpath.listFiles();
       for(int i=0; i<childs.length; ++i) {
         File child = childs[i];
         if (child.isDirectory()) {
           bd = findBuildDescriptionInSubdirs(rd,child,buildDir,organization,name);
           if (bd!=null) {
             return bd;
           }
         }
       }
       return null;
    }
  }

  private BuildDescriptionEntry findBuildDescriptionFinal(
                                                  RootBuildDirectoryEntry rd,
                                                  File fpath,
                                                  String buildDir,
                                                  String organization,
                                                  String name) 
   {
     if (!fpath.isDirectory()) return null;
     File target = new File(fpath, buildDir);
     if (!target.exists()) {
        return null;
     }
     if (!target.isDirectory()) {
        Message.verbose("not a directory: "+target.getAbsolutePath());
        return null;
     }
     File antFileTarget = new File(target,rd.getAntFile());
     if (!antFileTarget.exists()) {
        Message.verbose("found directory: "+target.getAbsolutePath()+" but file "+rd.getAntFile()+" not exists");
        return null;
     }
     BuildDescriptionEntry retval = new BuildDescriptionEntry();
     retval.setName(name);
     retval.setOrganization(organization);
     try {
       retval.setBuildDirectory(target.getCanonicalPath());
     } catch (IOException ex) {
       Message.error("can't get canonical path for "+target+":"+ex.getMessage());
       return null;
     }
     retval.setPublishTarget(rd.getPublishTarget());
     retval.setAntFile(rd.getAntFile());
     return retval;
  }

  private Map/*<String,BuildDescriptionEntry>*/ buildDescriptions;
  private Map/*<RootBuildDirectoryEntry>*/ rootBuildDirectories;

}
