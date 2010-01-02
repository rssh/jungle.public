package ua.gradsoft.jungle.jabsorbservlet;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jabsorb.JSONRPCBridge;
import org.jabsorb.JSONRPCServlet;
import ua.gradsoft.jungle.auth.server.AuthServerApiProvider;

 /**
  *Servlet, which call JSONORB with authorization and access checking
  * before call.
  *
  *Serlvet parameters:
  *<ul>
  *  <li> authApiProvider </li>    set name of bean for authApiProvider. Bean must
  * be registered in JSONRPCBridge (usially in spring initializer).
  *  <li> debugLevel </li>  debug level (int from 0 to 9)
  *</ul>
  *
  **/
public class JsonRpcWithAuthServlet extends JSONRPCServlet
{


    @Override
    public void init(ServletConfig config) throws ServletException
    {
      super.init(config);

      authServerApiProviderName_ = config.getInitParameter("authApiProvider");
      String sDebugLevel = config.getInitParameter("debugLevel");
      if (sDebugLevel!=null) {
          try {
              debugLevel_=Integer.parseInt(sDebugLevel);
          }catch(NumberFormatException ex){
              Log log = LogFactory.getLog(JsonRpcWithAuthServlet.class);
              log.error("value of debugLevel ("+sDebugLevel+") is not int, set to max");
              debugLevel_=9;
          }
      }
    }

    @Override
    protected JSONRPCBridge findBridge(HttpServletRequest request) {
        JSONRPCBridge origin = super.findBridge(request);
        AuthInvocationCallback authCallback = getAuthInvocationCallback();
        AuthServerApiProvider apiProvider = getAuthServerApiProvider(origin);
        authCallback.setAuthServerApiProvider(apiProvider);
        authCallback.setDebugLevel(debugLevel_);
        return new JsonRpcAuthProxyBridge(origin,
                                          getAuthInvocationCallback());
    }

    private AuthInvocationCallback getAuthInvocationCallback()
    {
       if (authInvocationCallback_==null) {
           authInvocationCallback_=new AuthInvocationCallback();
       } 
       return authInvocationCallback_;
    }

    private AuthServerApiProvider getAuthServerApiProvider(JSONRPCBridge bridge)
    {
     if (authServerApiProvider_==null) {   
       if (authServerApiProviderName_!=null) {   
         Object o = bridge.lookupObject(authServerApiProviderName_);
         if (o==null) {
             Log log = LogFactory.getLog(JsonRpcWithAuthServlet.class);
             log.error("authServerApiProvider ("+authServerApiProviderName_+") not found");
             throw new RuntimeException("AuthServerApiProvider not found");
         }else{
             authServerApiProvider_=(AuthServerApiProvider)o;
         }
       }else{
         Log log = LogFactory.getLog(JsonRpcWithAuthServlet.class);
         log.error("authServerApiProviderName is not set ");
         throw new RuntimeException("AuthServerApiProviderName is not set");       
       }
     }
     return authServerApiProvider_;
    }

    private AuthInvocationCallback authInvocationCallback_ = null;
    private String                 authServerApiProviderName_ = null;
    private AuthServerApiProvider  authServerApiProvider_ = null;
    private int                    debugLevel_=1;

}

