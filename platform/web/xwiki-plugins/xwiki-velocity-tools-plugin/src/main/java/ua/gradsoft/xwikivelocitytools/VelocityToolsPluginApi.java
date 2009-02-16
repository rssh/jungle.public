package ua.gradsoft.xwikivelocitytools;

import com.xpn.xwiki.XWikiContext;
import com.xpn.xwiki.api.Api;
import org.apache.velocity.tools.generic.DateTool;
import org.apache.velocity.tools.generic.EscapeTool;
import org.apache.velocity.tools.generic.ListTool;
import org.apache.velocity.tools.generic.MathTool;
import org.apache.velocity.tools.generic.NumberTool;
import org.apache.velocity.tools.generic.RenderTool;
import org.apache.velocity.tools.generic.ResourceTool;

public class VelocityToolsPluginApi extends Api
{

  public VelocityToolsPluginApi(VelocityToolsPlugin thePlugin,
                                XWikiContext context)
  {
    super(context);  
    plugin=thePlugin;  
  }


  public MathTool    getMathTool()
  { 
    if (mathTool_==null) {  
      mathTool_ = plugin.getMathTool(); 
    }
    return mathTool_;
  }
  
  public DateTool    getDateTool()
  {
      if (dateTool_==null) {
        dateTool_ = plugin.getDateTool(); 
      }
      return dateTool_;
  }

  public EscapeTool  getEscapeTool()
  {
      if (escapeTool_==null) {
          escapeTool_ = plugin.getEscapeTool();
      }   
      return escapeTool_;
  }
  
  public RenderTool   getRenderTool()
  {
      if (renderTool_==null) {
          renderTool_=plugin.getRenderTool();
      }
      return renderTool_;
  }
  
  public ResourceTool  getResourceTool()
  {
      if (resourceTool_==null) {
          resourceTool_=plugin.getResouceTool();
      }
      return resourceTool_;
  }

  public ListTool getListTool()
  {
      if (listTool_==null) {
          listTool_=plugin.getListTool();
      }
      return listTool_;
  }
  
  public NumberTool getNumberTool()
  {
      if (numberTool_==null) {
          numberTool_ = plugin.getNumberTool();
      }
      return numberTool_;
  }

  public ClassTool getClassTool()
  {
      if (classTool_==null) {
          classTool_ = plugin.getClassTool();
      }
      return classTool_;
  }
  
  
 private VelocityToolsPlugin plugin;
 
 private NumberTool          numberTool_=null;
 private MathTool            mathTool_=null;
 private DateTool            dateTool_=null;
 private EscapeTool          escapeTool_=null;
 private RenderTool          renderTool_=null;
 private ResourceTool        resourceTool_=null;
 private ListTool            listTool_=null;
 
 private ClassTool           classTool_=null;
  
}
