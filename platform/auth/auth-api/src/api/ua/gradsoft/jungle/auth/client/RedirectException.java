package ua.gradsoft.jungle.auth.client;

/**
 *Throwed from API to show redirect to other site.
 * @author rssh
 */
public class RedirectException extends Exception
{

    public RedirectException(String uri)
    { uri_=uri; }

    public String getUri()
    { return uri_; }

    private String uri_;
}