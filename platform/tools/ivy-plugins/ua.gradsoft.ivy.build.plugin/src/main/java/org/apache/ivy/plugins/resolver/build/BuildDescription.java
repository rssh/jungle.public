package org.apache.ivy.plugins.resolver.build;

import java.util.HashMap;
import java.util.Map;
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
    return null;
  }

  private Map/*<String,BuildDescriptionEntry>*/ buildDescriptions;
  private Map/*<RootBuildDirectoryEntry>*/ rootBuildDirectories;

}
