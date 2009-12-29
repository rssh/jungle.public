
package ua.gradsoft.jungle.jabsorbservlet;

import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Method;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jabsorb.callback.InvocationCallback;
import ua.gradsoft.jungle.auth.client.AuthException;
import ua.gradsoft.jungle.auth.server.AuthClientApiHttpRequestScopeImpl;
import ua.gradsoft.jungle.auth.server.AuthServerApiHelper;
import ua.gradsoft.jungle.auth.server.AuthServerApiProvider;
import ua.gradsoft.jungle.auth.server.UserServerContext;

/**
 *Callback with check invocation
 * @author rssh
 */
public class AuthInvocationCallback implements InvocationCallback
{


   public void preInvoke(Object context, Object instance,
                         AccessibleObject accessibleObject,
                         Object[] arguments) throws Exception {
        if (context instanceof HttpServletRequest) {
            HttpServletRequest r = (HttpServletRequest)context;
            UserServerContext usc;
            if (authServerApiProvider_!=null) {
                HttpSession hs = r.getSession(true);
                Object o = hs.getAttribute("lastId");
                if (o==null) {
                    // we have no attibure
                    usc=authServerApiProvider_.getAnonimousContext();
                }else if (o instanceof UserServerContext) {
                    usc = (UserServerContext)o;
                }else if (o instanceof String) {
                    usc = authServerApiProvider_.findContextById((String)o);
                    if (usc==null) {
                        throw new AuthException("bad session attribute, access denyed");
                    }
                }else{
                    Log log = LogFactory.getLog(AuthInvocationCallback.class);
                    log.error("bad session attribute, access denyed");
                    throw new AuthException("bad session attribute, access denyed");
                }

                if (accessibleObject instanceof Method) {
                   Method method = (Method)accessibleObject;
                   if (instance instanceof AuthClientApiHttpRequestScopeImpl) {
                      // call to instance of AuthClientApi must be allowed for all
                      return;
                   }
                   if (!AuthServerApiHelper.checkMethodPermissions(method,arguments, usc)) {
                      if (debugLevel_ > 0) {
                          Log log = LogFactory.getLog(AuthInvocationCallback.class);
                          log.info("Access denied for method " + method.getDeclaringClass().getName()+"."+method.getName()+" to user "+usc.getId());
                      }
                      throw new AuthException("Access denied for method "+method.getDeclaringClass().getName()+"."+method.getName()+" to user "+usc.getId());
                    }else{
                       // all ok
                       return;
                    }
                }else{
                  if (debugLevel_ > 0) {
                      Log log = LogFactory.getLog(AuthInvocationCallback.class);
                      log.info("Attempt to call non-method:"+accessibleObject.toString());
                  }
                  throw new AuthException("Attempt to call non-method:"+accessibleObject.toString());
                }

            }
        }else{
            if (debugLevel_>0) {
               Log log = LogFactory.getLog(AuthInvocationCallback.class);
               log.info("Deny non-http request");
            }
            throw new AuthException("Deny non-http request");
        }
   }

   public void postInvoke(Object o, Object o1, AccessibleObject ao, Object o2) throws Exception {

   }

   public AuthServerApiProvider getAuthServerApiProvider()
   {
      return authServerApiProvider_; 
   }

   public void setAuthServerApiProvider(AuthServerApiProvider authServerApiProvider)
   {
       authServerApiProvider_ = authServerApiProvider;
   }

   public int getDebugLevel()
   { return debugLevel_; }
   
   public void setDebugLevel(int debugLevel)
   {
     debugLevel_=debugLevel;  
   }

   private AuthServerApiProvider authServerApiProvider_;
   private int                   debugLevel_;

}
