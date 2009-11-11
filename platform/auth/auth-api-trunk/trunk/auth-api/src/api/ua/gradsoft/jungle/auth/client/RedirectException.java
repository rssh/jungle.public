package ua.gradsoft.jungle.auth.client;

import java.io.Serializable;

/**
 *Throwed from API to show redirect to other site.
 * @author rssh
 */
public class RedirectException extends Exception implements Serializable
{

    /**
     * default constructor needed for serializing.
     */
    public RedirectException()
    {}

    /**
     * construct exception with target uri <code> uri </code>.
     */
    public RedirectException(String uri)
    { uri_=uri; }

    /**
     * get uri, where redirect must go.
     */
    public String getUri()
    { return uri_; }

    private String uri_;
}
