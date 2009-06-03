package ua.gradsoft.jungle.auth.client;

import java.io.Serializable;

/**
 *Exception is throwed when auth subsystem can't configrm information.
 * @author rssh
 */
public class AuthException extends Exception implements Serializable
{

    public AuthException(String message)
    {
       super(message);
    }

    public AuthException(String message, Exception ex)
    {
       super(message,ex);
    }


}