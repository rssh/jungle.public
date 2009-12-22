package ua.gradsoft.jungle.persistence.jpaex;

/**
 *Runtie wrapper for checked exceptions, which can be
 * throwed during JPA-related operations.
 * @author rssh
 */
public class JPASystemException extends RuntimeException
{

    public JPASystemException(String message, Exception ex)
    { super(message,ex); }

}
