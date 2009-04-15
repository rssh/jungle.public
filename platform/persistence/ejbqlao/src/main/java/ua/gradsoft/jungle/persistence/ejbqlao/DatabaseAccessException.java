package ua.gradsoft.jungle.persistence.ejbqlao;

/**
 * Exception, which throwed on failure in underlaying persistence mechanizms
 */
public class DatabaseAccessException extends RuntimeException
{
   public DatabaseAccessException(String message, Exception ex)
   {
     super(message,ex);
   }

}
