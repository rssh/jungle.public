
package ua.gradsoft.persistence.ejbqlao;

import java.util.HashMap;
import java.util.Map;

/**
 *Configuration for ejb/ql object.
 * @author rssh
 */
public class EjbQlAccessConfiguration 
{

    public Map<String,Object>  getOptions()
    { return options_; }
    
    private Map<String,Object> options_ = new HashMap<String,Object>();
    
}
