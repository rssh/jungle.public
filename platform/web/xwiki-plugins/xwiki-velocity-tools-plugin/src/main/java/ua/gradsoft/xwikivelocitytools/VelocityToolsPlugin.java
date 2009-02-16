package ua.gradsoft.xwikivelocitytools;

import com.xpn.xwiki.XWikiContext;
import com.xpn.xwiki.api.Api;
import com.xpn.xwiki.plugin.XWikiDefaultPlugin;
import com.xpn.xwiki.plugin.XWikiPluginInterface;
import org.apache.velocity.tools.generic.DateTool;
import org.apache.velocity.tools.generic.EscapeTool;
import org.apache.velocity.tools.generic.ListTool;
import org.apache.velocity.tools.generic.MathTool;
import org.apache.velocity.tools.generic.NumberTool;
import org.apache.velocity.tools.generic.RenderTool;
import org.apache.velocity.tools.generic.ResourceTool;


/**
 * this is workarrounf
 * 
 * @author rssh
 */

public class VelocityToolsPlugin extends XWikiDefaultPlugin
                                 implements XWikiPluginInterface
{

   public VelocityToolsPlugin(String name, String className, XWikiContext context) {
     super(name,className,context);
  }

  /**
   * "tools"
   *@return tools
   **/
  public String getName()
  { return "tools"; }


  public Api  getPluginApi(XWikiPluginInterface plugin, XWikiContext context)
  { return new VelocityToolsPluginApi((VelocityToolsPlugin)plugin,context); }  

  /**
   * @deprecated   
   */
  @Override
  public void flushCache()
  {}

  public void init(XWikiContext context) //throws XWikiException
  {
        super.init(context);
  }

  public NumberTool  getNumberTool()
    { return new NumberTool(); }

  public MathTool  getMathTool()
    { return new MathTool(); }

  public DateTool  getDateTool()
  { return new DateTool(); }
  
  public EscapeTool  getEscapeTool()
  { return new EscapeTool(); }

  public RenderTool  getRenderTool()
  { return new RenderTool(); }
  
  public ResourceTool  getResouceTool()
  { return new ResourceTool(); }
  
  public ListTool  getListTool()
  { return new ListTool(); }
  
  public ClassTool  getClassTool()
  { return new ClassTool(); }
  
}
