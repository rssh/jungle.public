
package ua.gradsoft.xwikinaming;


import javax.naming.Context;
import javax.naming.NamingException;


/**
 *Generic interface for naming provider.
 * (JNDI or Ioc framework, such as spring)
 * @author rssh
 */
public interface NamingProvider {

    void   init(Configuration configuration) throws NamingException;
    
    Context getContext();    
    
}
