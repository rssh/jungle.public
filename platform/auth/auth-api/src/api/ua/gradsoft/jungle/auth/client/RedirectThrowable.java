package ua.gradsoft.jungle.auth.client;

import java.io.Serializable;

/**
 *Throwed from API to show redirect to other site.
 * @author rssh
 */
public class RedirectThrowable extends Throwable implements Serializable
{

    public RedirectThrowable(String uri)
    { uri_=uri; }

    public String getUri()
    { return uri_; }

    private String uri_;
}
