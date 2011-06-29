package org.apache.ivy.plugins.resolver.build;


public class RootBuildDirectoryEntry {

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getBuildDirPattern() {
        return buildDirPattern;
    }

    public void setBuildDirPattern(String buidlDirPattern) {
        this.buildDirPattern = buildDirPattern;
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


    public boolean getCheckSubdirs()
     { return checkSubdirs; }

    public void setCheckSubdirs(boolean checkSubdirs)
     { this.checkSubdirs=checkSubdirs; }


    private String path;
    private String buildDirPattern;
    private String publishTarget = "publish";
    private String antFile = "build.xml";
    private boolean checkSubdirs = true;


}
