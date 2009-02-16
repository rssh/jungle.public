package ua.gradsoft.persistence.ejbqlao;

/**
 *Exception 
 * @author rssh
 */
public class ObjectParseException extends RuntimeException
{

    public ObjectParseException(String message)
    {
      super(message);  
    }
    
    public ObjectParseException(String message, Exception ex)
    {
      super(message,ex);  
    }
    
    
}
