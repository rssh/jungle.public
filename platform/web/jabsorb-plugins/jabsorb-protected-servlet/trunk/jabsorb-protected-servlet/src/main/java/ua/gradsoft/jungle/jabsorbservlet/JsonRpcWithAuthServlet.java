package ua.gradsoft.jungle.jabsorbservlet;

import java.io.IOException;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import org.jabsorb.JSONRPCBridge;
import org.jabsorb.JSONRPCServlet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
              Logger log = LoggerFactory.getLogger(JsonRpcWithAuthServlet.class);
              log.error("value of debugLevel ({}) is not int, set to max",sDebugLevel);
              debugLevel_=9;
          }
      }
      authClientApiName_ = config.getInitParameter("authApiName");
      if (authClientApiName_==null) {
          authClientApiName_="auth";
      }
    }

    @Override
    public void service(HttpServletRequest request, HttpServletResponse response) throws IOException {
        //SONRPCBridge bridge = findBridge(request);
        super.service(request, response);
        HttpSession hs = request.getSession(false);
        if (hs!=null) {
            Object o = hs.getAttribute("JSON_ACCEPT");
            if (o!=null) {
              if (debugLevel_>=7) {
                Logger log = LoggerFactory.getLogger(JsonRpcWithAuthServlet.class);
                log.info("JSON_ACCEPT is {}",o);
              }
              if (o instanceof Boolean) {
                 Boolean b = (Boolean)o;
                 if (!b) {
                     response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                 }
              }
            }
        }
 
        //bridge.getCallbackController().unregisterCallback(authCallback, HttpServletRequest.class);
    }



    
    @Override
    protected JSONRPCBridge findBridge(HttpServletRequest request) {
        JSONRPCBridge origin = super.findBridge(request);
        AuthInvocationCallback authCallback = getAuthInvocationCallback();
        if (authCallback.getAuthServerApiProvider()==null) {
          AuthServerApiProvider apiProvider = getAuthServerApiProvider(origin);
          authCallback.setAuthServerApiProvider(apiProvider);
          authCallback.setDebugLevel(debugLevel_);
        }
        return new JsonRpcAuthProxyBridge(origin,
                                          authCallback,
                                          authClientApiName_, request,
                                          debugLevel_);
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
             Logger log = LoggerFactory.getLogger(JsonRpcWithAuthServlet.class);
             log.error("authServerApiProvider ({}) not found", authServerApiProviderName_);
             throw new RuntimeException("AuthServerApiProvider not found");
         }else{
             authServerApiProvider_=(AuthServerApiProvider)o;
         }
       }else{
         Logger log = LoggerFactory.getLogger(JsonRpcWithAuthServlet.class);
         log.error("authServerApiProviderName is not set ");
         throw new RuntimeException("AuthServerApiProviderName is not set");       
       }
     }
     return authServerApiProvider_;
    }

    private AuthInvocationCallback authInvocationCallback_ = null;
    private String                 authServerApiProviderName_ = null;
    private AuthServerApiProvider  authServerApiProvider_ = null;
    private String                 authClientApiName_=null;
    private int                    debugLevel_=1;

}

