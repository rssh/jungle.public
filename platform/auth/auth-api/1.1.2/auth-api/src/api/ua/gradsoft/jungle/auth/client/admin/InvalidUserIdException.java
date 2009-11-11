
package ua.gradsoft.jungle.auth.client.admin;

/**
 *Exceptions with
 * @author rssh
 */
public class InvalidUserIdException extends Exception
{
    
    // rerquired by gwt
    public InvalidUserIdException()
    {}

    public InvalidUserIdException(String userId)
    {
      super("Invalid user id:"+userId);
    }

}
