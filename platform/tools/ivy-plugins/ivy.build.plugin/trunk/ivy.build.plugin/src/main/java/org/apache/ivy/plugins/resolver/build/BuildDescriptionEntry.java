package org.apache.ivy.plugins.resolver.build;


public class BuildDescriptionEntry {

    public String getName() {
        return name;
    }

    public void setName(String theName) {
        name = theName;
    }

    public String getOrganization() {
        return organization;
    }

    public void setOrganization(String theOrganization) {
        organization = theOrganization;
    }

    public String getBuildDirectory() {
        return buildDirectory;
    }

    public void setBuildDirectory(String theBuildDirectory) {
        buildDirectory = theBuildDirectory;
    }

    public String getPublishTarget() {
        return publishTarget;
    }

    public void setPublishTarget(String thePublishTarget) {
        publishTarget = thePublishTarget;
    }

    public String getAntFile() {
        return antFile;
    }

    public void setAntFile(String theAntFile) {
        antFile = theAntFile;
    }


    boolean isBuildWasCalled()
    { return buildWasCalled; }
    
    void markCallOfBuild()
    { buildWasCalled=true; }


    private String organization;
    private String name;
    private String buildDirectory;
    private String publishTarget = "publish";
    private String antFile = "build.xml";

    private boolean  buildWasCalled = false;

}
