
package ua.gradsoft.jungle.persistence.jpaex;

import java.io.IOException;

/**
 *runtime wrapped for IOException, which can be thrown during object
 *  serialization/deserialization
 */
public class JPAIOException extends RuntimeException
{

    public JPAIOException(IOException ex)
    {
      super("Exception during serialization", ex);  
    }

}
