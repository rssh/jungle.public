package ua.gradsoft.caching;

/**
 *System exception, which thrown during build of caching infrastructure.
 */
public class CachingSystemException extends RuntimeException
{

    public CachingSystemException(String message)
    {
     super(message);
    }


    public CachingSystemException(String message, Exception ex)
    {
     super(message,ex);
    }



}
