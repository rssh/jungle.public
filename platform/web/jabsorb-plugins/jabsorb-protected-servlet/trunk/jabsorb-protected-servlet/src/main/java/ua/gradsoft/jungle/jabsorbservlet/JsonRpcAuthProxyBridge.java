
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
      registerCallback(callback,HttpServletRequest.class);
      registerObject(authClientApiName,
                     new AuthClientApiHttpRequestScopeImpl(callback.getAuthServerApiProvider(),
                                                           request)
                     );
      //authClientApiName_ = authClientApiName;
      //debugLevel_=debugLevel;
      //System.err.println("callback and object registered");
    }

    @Override
    public Object lookupObject(Object key) {
        Object retval = super.lookupObject(key);
        if (retval==null) {
            retval=origin_.lookupObject(key);
        }
        return retval;
    }





    //private HttpRequest   request_;
    private JSONRPCBridge origin_;
    //private AuthServerApiProvider authApiProvider_;
    //private HttpServletRequest   request_;
    //private int                  debugLevel_;

}
