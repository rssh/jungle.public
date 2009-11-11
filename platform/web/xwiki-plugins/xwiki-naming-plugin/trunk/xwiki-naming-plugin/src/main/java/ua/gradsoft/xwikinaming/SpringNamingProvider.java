package ua.gradsoft.xwikinaming;

import java.util.Hashtable;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import org.apache.xbean.spring.jndi.SpringInitialContextFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 *Naming provider for spring framework
 * @author rssh
 */
public class SpringNamingProvider implements NamingProvider
{

    public void init(Configuration configuration) throws NamingException
    {      
      // init spring  
      String[] springConfig = new String[1];
      springConfig[0] = configuration.getSpringConfig();
      springApplicationContext_ = new ClassPathXmlApplicationContext(springConfig);
      Hashtable env = new Hashtable();
      env.put(Context.INITIAL_CONTEXT_FACTORY, SpringInitialContextFactory.class.getName());
      env.put(Context.PROVIDER_URL, "classpath:"+springConfig[0]);
      initialContext_ = new InitialContext(env);   
    }

    public Context getContext()
    { return initialContext_; }
    
    private Context initialContext_;
    private ApplicationContext springApplicationContext_;
   
    
    
}
