package ua.gradsoft.jungle.auth.client;

import java.io.Serializable;

/**
 *Exception is throwed when auth subsystem can't configrm information.
 * @author rssh
 */
public class AuthException extends Exception implements Serializable
{

    /**
     * default constructors needs to gwt-serializing.
     */
    public AuthException()
    {

    }

    /**
     * Construct exception with given message.
     */
    public AuthException(String message)
    {
       super(message);
    }

    /**
     * Construct exception with given message and cause
     */
    public AuthException(String message, Exception ex)
    {
       super(message,ex);
    }


}
