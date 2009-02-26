package ua.gradsoft.jungle.configuration;

import java.io.Serializable;

/**
 * Marker interface for selectors of config items.
 **/ 
public abstract class ConfigItemSelector implements Serializable
{
    
    public int getFirstResult()
    { return firstResult_; }
    
    public void setFirstResult(int firstResult)
    { firstResult_=firstResult; }
    
    public int getMaxResults()
    { return maxResults_; }

    public void setMaxResults(int maxResults)
    { maxResults_=maxResults; }

    private int firstResult_;
    private int maxResults_;
}
