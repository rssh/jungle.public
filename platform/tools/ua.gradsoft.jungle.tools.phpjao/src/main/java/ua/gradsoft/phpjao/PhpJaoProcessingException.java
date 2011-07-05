package ua.gradsoft.phpjao;

/**
 *Exception, throwed during processing.
 * @author rssh
 */
public class PhpJaoProcessingException extends Exception
{

    public PhpJaoProcessingException(String message)
    { super(message); }
    
    public PhpJaoProcessingException(String message, Exception ex)
    { super(message,ex); }

}
