package org.apache.ivy.plugins.resolver.build;

import java.util.HashMap;
import java.util.Map;
import org.apache.ivy.util.Message;


public class BuildDescription
{

  public BuildDescription()
  {
   buildDescriptions = new HashMap/*<String,BuildDescriptionEntry>*/();  
  }

  public void addEntry(BuildDescriptionEntry entry)
  {
    String key = createKey(entry.getOrganization(), entry.getName());
    buildDescriptions.put(key, entry);
  }


  /**
   * return build description or null, if one not found.
   */
  public BuildDescriptionEntry findBuildDescription(String organization, String name)
  {
    String key = createKey(organization,name);
    return (BuildDescriptionEntry)buildDescriptions.get(key);
  }

  private String createKey(String organization, String name)
  {
      return organization+"/"+name;
  }


  private Map/*<String,BuildDescriptionEntry>*/ buildDescriptions;

}
