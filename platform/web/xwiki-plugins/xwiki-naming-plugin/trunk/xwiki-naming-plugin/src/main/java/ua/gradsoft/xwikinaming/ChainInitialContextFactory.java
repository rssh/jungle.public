
package ua.gradsoft.xwikinaming;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Hashtable;
import java.util.Map;
import java.util.Properties;
import javax.naming.ConfigurationException;
import javax.naming.Context;
import javax.naming.InvalidNameException;
import javax.naming.Name;
import javax.naming.NameParser;
import javax.naming.NamingException;
import javax.naming.spi.InitialContextFactory;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;


/**
 *ContextFactory for chain.
 * @author rssh
 */
public class ChainInitialContextFactory implements InitialContextFactory
{
    
    public static final String FIRST_PREFIX = "jndi.chain.first.";
    public static final String SECOND_PREFIX = "jndi.chain.second.";
    
    public ChainInitialContextFactory(ICFRecord frs, ICFRecord snd)
    {
      frs_=frs;
      snd_=snd;
    }

    public ChainInitialContextFactory()
    {
      frs_=null;
      snd_=null;
    }
    
    
    public Context getInitialContext(Hashtable env) throws NamingException
    {
       if (frs_==null) {
           frs_ = createInitialContextByProperties(env,FIRST_PREFIX);
       } 
       if (snd_==null) {
           snd_ = createInitialContextByProperties(env,SECOND_PREFIX);
       }
       Context frsc = frs_.getInitialContext();
       Context sndc = snd_.getInitialContext();
       return new ChainContext(frsc,sndc,frs_.isWritable(),snd_.isWritable(),this);
    }
    
    private ICFRecord createInitialContextByProperties(Hashtable env, String prefix) throws ConfigurationException
    {
       Object o = env.get(prefix);
       InitialContextFactory f = null;  
       Hashtable nenv = new Hashtable();
       if (o!=null) {
           if (o instanceof InitialContextFactory) {
             f = (InitialContextFactory)o;  
           }else{
             throw new ConfigurationException("type of '"+prefix+"' env entry is invalid");  
           }           
       }
       for(Object oe : env.entrySet()) {
          Map.Entry e = (Map.Entry)oe;
          String s = (String)e.getKey();
          if (s.startsWith(prefix)) {
              nenv.put(s.substring(prefix.length()),e.getValue());
          }
       }
       //  check that wi have <prefix>.java.naming,factory.initial
       //java.naming.factory.initial
       //java.naming.factory.object
       //java.naming.factory.state
       //java.naming.factory.control
       //java.naming.factory.url.pkgs
       //java.naming.provider.url
       //java.naming.dns.url	       
       if (f==null) {
                    
           o = nenv.get("java.naming.factory.initial");
           if (o==null) {
               o=System.getenv(prefix+Context.INITIAL_CONTEXT_FACTORY);
           }
           if (o==null) {
               o=System.getenv(Context.INITIAL_CONTEXT_FACTORY);
           }
           if (o==null) {
               Properties prop = System.getProperties();
               if (prop!=null) {
                  o=prop.getProperty(Context.INITIAL_CONTEXT_FACTORY);
               }
           }

           
           if (o==null) {                          
               throw new ConfigurationException("can't get initial context: "+Context.INITIAL_CONTEXT_FACTORY+" is not set");
           }else{           
               String cname = o.toString();
               try {
                 Class fclass = Class.forName(cname);                 
                 Constructor cn = fclass.getConstructor(new Class[0]);
                 o = cn.newInstance(new Object[0]);                       
                 if (o instanceof InitialContextFactory) {
                   f = (InitialContextFactory)o;
                 }else{
                   throw new ConfigurationException("class "+cname+" doew not implement InitialContextFactory");
                 }
               }catch(ClassNotFoundException ex) {
                   throw new ConfigurationException("class "+cname+" is not found");
               }catch(NoSuchMethodException ex){
                   throw new ConfigurationException("default constructor for class "+cname+" is not found");
               }catch(InstantiationException ex){
                   LOG.error("exception during sstantiation of "+cname, ex);
                   throw new ConfigurationException("Can't instantiate "+cname+":"+ex.getMessage());
               }catch(InvocationTargetException ex){
                   LOG.error("exception during istantiation of "+cname, ex);
                   throw new ConfigurationException("Can't invoke constructor of "+cname+":"+ex.getMessage());                   
               }catch(IllegalAccessException ex){
                   throw new ConfigurationException("Constructor for "+cname+" is not public");
               }
           }
       }
       boolean writable = false;
       if (nenv.get("writable")!=null) {
          Object v = nenv.get("writable");
          String sv = v.toString();
          if (sv.equals("1")||sv.equalsIgnoreCase("true")||sv.equalsIgnoreCase("yes")) {
              writable = true;
          }else if (sv.equals("0")||sv.equalsIgnoreCase("false")||sv.equalsIgnoreCase("no")) {
              writable = false;
          }else{
              throw new ConfigurationException("Can't transform value of "+prefix+"writable to boolean");
          }
       }
       ICFRecord retval = new ICFRecord(f,writable,nenv);
       return retval;
    }
    
   class ChainNameParser implements NameParser
   {
     public ChainNameParser(NameParser p1, NameParser p2)
     { p1_=p1; p2_=p2; }
     
     public Name parse(String name) throws NamingException
     {
       try {
           return p1_.parse(name);
       }catch(InvalidNameException ex){
           return p2_.parse(name);
       }
     }
     
     private NameParser p1_;  
     private NameParser p2_;  
     
     
   }

    public NameParser getChainNameParser(Name name, Context frs, Context snd) throws NamingException
    {
        if (nameParser_==null) {
            nameParser_=new ChainNameParser(frs.getNameParser(name),snd.getNameParser(name));
        }
        return nameParser_;
    }
   
   
    public NameParser getChainNameParser(String name, Context frs, Context snd) throws NamingException
    {
        if (nameParser_==null) {
            nameParser_=new ChainNameParser(frs.getNameParser(name),snd.getNameParser(name));
        }
        return nameParser_;
    }

   
     ICFRecord frs_;
     ICFRecord snd_;
    
     public static class ICFRecord
     {
         ICFRecord(InitialContextFactory factory, boolean writable, Hashtable env)
         {
           factory_=factory; 
           isWritable_=writable;
           env_=env;
         }
         
         public InitialContextFactory getFactory()
         { return factory_; }
         
         public boolean isWritable()
         { return isWritable_; }
         
         public Context  getInitialContext() throws NamingException
         {
           return factory_.getInitialContext(env_);  
         }
         
         private InitialContextFactory factory_;
         private boolean               isWritable_;
         private Hashtable             env_;
     }
    
    private ChainNameParser nameParser_=null;
    
    private static Log LOG = LogFactory.getLog(ChainInitialContextFactory.class);
    
}
