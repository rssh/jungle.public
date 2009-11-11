
package ua.gradsoft.phpjao;

/**
 *Exception, which occured during configuration
 * @author rssh
 */
public class PhpJaoConfigException extends Exception
{

    public PhpJaoConfigException(String message)
    { super(message); }

    public PhpJaoConfigException(String message, Exception ex)
    { super(message, ex); }



}
