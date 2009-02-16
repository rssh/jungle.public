package ua.gradsoft.xwikinaming;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Hashtable;
import java.util.Map;
import java.util.Properties;
import javax.naming.ConfigurationException;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 *Naming provider which read names from initial JNDI
 * context of J2EE environment.
 * @author rssh
 */
public class JNDINamingProvider implements NamingProvider {

    public void init(Configuration configuration) throws NamingException {
        LOG.debug("JNDINamingProvider.init begin");
        Hashtable chainParams = new Hashtable();
        boolean initSpring = false;
        LOG.debug("JNDINamingProvider.init-x1");
        if (configuration.isUseChain()) {
            LOG.debug("JNDINamingProvider.init-x2");
            String fname = configuration.getChainFirstPropertiesFile();
            LOG.debug("JNDINamingProvider.init-x3");
            if (fname != null) {
                Properties p = new Properties();
                try {
                    p.load(new FileInputStream(fname));
                } catch (FileNotFoundException ex) {
                    throw new ConfigurationException("File " + fname + " is not found");
                } catch (IOException ex) {
                    throw new ConfigurationException("Can't read file " + fname + ":" + ex.getMessage());
                }
                for (Map.Entry e : p.entrySet()) {
                    chainParams.put(ChainInitialContextFactory.FIRST_PREFIX + e.getKey(), e.getValue());
                }
            }
            LOG.debug("JNDINamingProvider.init-x4");
            if (configuration.getChainFirstSpring()) {
               LOG.debug("JNDINamingProvider.init-x5"); 
               initSpring=true;
               LOG.debug("JNDINamingProvider.init-x6"); 
               chainParams.put(ChainInitialContextFactory.FIRST_PREFIX+Context.INITIAL_CONTEXT_FACTORY,
                               "org.apache.xbean.spring.jndi.SpringInitialContextFactory");
               chainParams.put(ChainInitialContextFactory.FIRST_PREFIX+Context.PROVIDER_URL, 
                               "classpath:"+configuration.getSpringConfig());
            }
            LOG.debug("JNDINamingProvider.init-x7"); 
            if (configuration.getChainFirstDefault()) {
                //String fname = configuration.
            }
            LOG.debug("JNDINamingProvider.init-x8"); 
            if (configuration.getChainFirstWritable()) {
                chainParams.put(ChainInitialContextFactory.FIRST_PREFIX+"writable", "1");
            }
            LOG.debug("JNDINamingProvider.init-x9"); 
            fname = configuration.getChainSecondPropertiesFile();
            if (fname!=null) {
                Properties p = new Properties();
                try {
                    p.load(new FileInputStream(fname));
                } catch (FileNotFoundException ex) {
                    throw new ConfigurationException("File " + fname + " is not found");
                } catch (IOException ex) {
                    throw new ConfigurationException("Can't read file " + fname + ":" + ex.getMessage());
                }
                for (Map.Entry e : p.entrySet()) {
                    chainParams.put(ChainInitialContextFactory.SECOND_PREFIX + e.getKey(), e.getValue());
                }                
            }
            LOG.debug("JNDINamingProvider.init-x10"); 
            if (configuration.getChainSecondSpring()) {
               initSpring=true;
               chainParams.put(ChainInitialContextFactory.SECOND_PREFIX+Context.INITIAL_CONTEXT_FACTORY,
                               "org.apache.xbean.spring.jndi.SpringInitialContextFactory"
                               );
               chainParams.put(ChainInitialContextFactory.SECOND_PREFIX+Context.PROVIDER_URL, 
                               "classpath:"+configuration.getSpringConfig());
            }
            LOG.debug("JNDINamingProvider.init-x11"); 
            if (configuration.getChainSecondWritable()) {
                chainParams.put(ChainInitialContextFactory.SECOND_PREFIX+"writable", "1");                                
            }
            LOG.debug("JNDINamingProvider.init-x12"); 
            if (initSpring) {
                LOG.debug("JNDINamingProvider.init-x13"); 
                springNamingProvider = new SpringNamingProvider();
                LOG.debug("JNDINamingProvider.init-x14"); 
                springNamingProvider.init(configuration);
                LOG.debug("JNDINamingProvider.init-x15"); 
            }
            LOG.debug("JNDINamingProvider.init-x16"); 
            ChainInitialContextFactory factory = new ChainInitialContextFactory();
            LOG.debug("JNDINamingProvider.init-x17"); 
            initialContext_ = factory.getInitialContext(chainParams);
            LOG.debug("JNDINamingProvider.init-x18"); 
        }else{            
            initialContext_ = new InitialContext();    
        }
        LOG.debug("JNDINamingProvider.init end");
    }

    public Context getContext() {
        return initialContext_;
    }
    
    private static final Log LOG = LogFactory.getLog(JNDINamingProvider.class);
    private Context initialContext_;
    private SpringNamingProvider springNamingProvider=null;
    
}
