
package ua.gradsoft.xwikinaming;

import com.xpn.xwiki.XWiki;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 *Configuration for naming framework
 * @author rssh
 */
public class Configuration 
{

  public boolean isUseChain()
  { return useChain_; }
  
  public void  setUseChain(boolean value)
  {
    useChain_=value;  
  }
  
  public String getSpringConfig()
  {
    return springConfig_;  
  }
  
  public void  setSpringConfig(String value)
  {
    springConfig_=value;  
  }
  
  public boolean getChainFirstSpring()
  {
    return chainFirstSpring_;  
  }
  
  public void  setChainFirstSpring(boolean value)
  {
    chainFirstSpring_ = value;  
  }

  public boolean getChainFirstDefault()
  {
    return chainFirstDefault_;  
  }
  
  public void  setChainFirstDefault(boolean value)
  {
    chainFirstDefault_ = value;  
  }

  public boolean getChainFirstWritable()
  {
    return chainFirstWritable_;  
  }
  
  public void  setChainFirstWritable(boolean value)
  {
    chainFirstWritable_ = value;  
  }
 
  public boolean getChainSecondSpring()
  {
    return chainSecondSpring_;  
  }
  
  public void  setChainSecondSpring(boolean value)
  {
    chainSecondSpring_ = value;  
  }

  public boolean getChainSecondDefault()
  {
    return chainSecondDefault_;  
  }
  
  public void  setChainSecondDefault(boolean value)
  {
    chainSecondDefault_ = value;  
  }

  public boolean getChainSecondWritable()
  {
    return chainSecondWritable_;  
  }
  
  public void  setChainSecondWritable(boolean value)
  {
    chainSecondWritable_ = value;  
  }
  
  public String getChainFirstPropertiesFile()
  { return chainFirstPropertiesFile_; }
  
  public void   setChainFirstPropertiesFile(String fname)
  {
    chainFirstPropertiesFile_ = fname;  
  }

  public String getChainSecondPropertiesFile()
  { return chainSecondPropertiesFile_; }
  
  public void   setChainSecondPropertiesFile(String fname)
  {
    chainSecondPropertiesFile_ = fname;  
  }
  
  public void init(XWiki xwiki)
  {
    String sUseChain = xwiki.Param("xwikinaming.chain");
    if (sUseChain==null) {
      useChain_=false;
    } else {
      try {  
        useChain_ = xwiki.ParamAsLong("xwikinaming.chain")==0 ? false : true;
      }catch(NumberFormatException ex){
          LOG.error("xwikinaming.chain must be number (0 or 1)");        
          useChain_=false;
      }
    }
    if (useChain_) {
        String first = xwiki.Param("xwikinaming.chain.first");
        if (first!=null) {
            if (first.equals("spring")) {
                chainFirstSpring_=true;
            }else if (first.equals("default")) {
                chainFirstDefault_=true;
            }
        }
        String firstProperties=xwiki.Param("xwikinaming.chain.first.properties");
        if (firstProperties!=null) {
            chainFirstPropertiesFile_=firstProperties;
        }
        String second = xwiki.Param("xwikinaming.chain.second");
        if (second!=null) {
            if (second.equals("spring")) {
                chainSecondSpring_=true;
            }else if (second.equals("default")) {
                chainSecondDefault_=true;
            }           
        }
        String secondProperties=xwiki.Param("xwikinaming.chain.second.properties");
        if (secondProperties!=null) {
            chainSecondPropertiesFile_=secondProperties;
        }
    }
    String springConfig = xwiki.Param("xwikinaming.spring.config");
    if (springConfig!=null) {
        springConfig_=springConfig;
    }
  }
  
  private boolean useChain_ = false;
  private boolean chainFirstSpring_ = false;
  private boolean chainFirstDefault_ = false;
  private boolean chainFirstWritable_ = false;
  private boolean chainSecondSpring_ = false;
  private boolean chainSecondDefault_ = false;
  private boolean chainSecondWritable_ = false;
  
  private String chainFirstPropertiesFile_ = null;
  private String chainSecondPropertiesFile_ = null;
  
  private String  springConfig_ = "spring.xml";
  
  private static final Log LOG = LogFactory.getLog(Configuration.class);
         
}
