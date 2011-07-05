
package ua.gradsoft.jungle.auth.client.admin;

/**
 *This exception is throws on call of AuthAdminApi with Invalid auth parameters.
 * @author rssh
 */
public class InvalidAdminParametersException extends Exception
{

    // required by gwt
    public InvalidAdminParametersException()
    {}

    public InvalidAdminParametersException(String message)
    { super(message); }

    public InvalidAdminParametersException(String message, Exception ex)
    { super(message,ex); }


}
