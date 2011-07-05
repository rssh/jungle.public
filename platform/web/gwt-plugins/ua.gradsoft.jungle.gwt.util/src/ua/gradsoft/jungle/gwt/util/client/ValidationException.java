package ua.gradsoft.jungle.gwt.util.client;

/**
 *Exception coming from forms, when they can't be validated.
 * @author rssh
 */
public class ValidationException extends Exception
{

    public ValidationException(String message)
    {
      super(message);  
    }

}
