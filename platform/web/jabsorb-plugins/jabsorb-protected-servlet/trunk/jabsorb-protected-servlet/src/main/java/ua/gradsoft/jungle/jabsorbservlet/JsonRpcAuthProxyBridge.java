
package ua.gradsoft.jungle.jabsorbservlet;

import javax.servlet.http.HttpServletRequest;
import org.jabsorb.JSONRPCBridge;
import org.jabsorb.JSONRPCResult;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ua.gradsoft.jungle.auth.server.AuthClientApiHttpRequestScopeImpl;
import ua.gradsoft.jungle.auth.server.AuthServerApiProvider;

/**
 *ProxyBridge, which check user permissions and reject requests
 * or redirect one to origin bridge
 * @author rssh
 */
public class JsonRpcAuthProxyBridge extends JSONRPCBridge
{

    public JsonRpcAuthProxyBridge(JSONRPCBridge origin,
                                  AuthInvocationCallback callback,
                                  String authClientApiName,
                                  HttpServletRequest request,
                                  int debugLevel)
    {
      origin_=origin;
      origin_.registerCallback(callback,HttpServletRequest.class);
      origin_.registerObject(authClientApiName,
                     new AuthClientApiHttpRequestScopeImpl(callback.getAuthServerApiProvider(),
                                                           request_)
                     );
      //authClientApiName_ = authClientApiName;
      //debugLevel_=debugLevel;
      System.err.println("callback and object registered");
    }


    //private HttpRequest   request_;
    private JSONRPCBridge origin_;
    //private AuthServerApiProvider authApiProvider_;
    private HttpServletRequest   request_;
    //private int                  debugLevel_;

}
