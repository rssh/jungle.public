
package ua.gradsoft.jungle.auth.server;

import java.io.Serializable;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.TreeMap;
import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import ua.gradsoft.jungle.auth.client.AuthClientApi;
import ua.gradsoft.jungle.auth.client.AuthException;
import ua.gradsoft.jungle.auth.client.ClientUserInfo;
import ua.gradsoft.jungle.auth.client.InvalidSessionTicketException;
import ua.gradsoft.jungle.auth.client.RedirectException;

/**
 *Implementation in AuthClientApi, which exists in request scope.
 */
public class AuthClientApiHttpRequestScopeImpl implements AuthClientApi
{

    /**
     * Session ticket stored in session context
     */
    @Permission(name="*")
    @Override
    public String getSessionTicket(String authType, Map<String,String> parameters) throws RedirectException, AuthException {
        UserServerContext ctx = apiProvider_.findAuthenticatedUserContext(authType, parameters);
        if (ctx==null) {
            throw new AuthException("Authorization failed: user not found");
        }
        HttpSession session = request_.getSession(true);
        UserRecord ur = new UserRecord(ctx.getId());
        String sessionTicket = ur.getSessionTicket();
        session.setAttribute(sessionTicket, ur);
        session.setAttribute("lastUserId", ur.userId_);
        session.setAttribute("lastUserTicket", sessionTicket);
        return ur.getSessionTicket();
    }

    @Override
    public  String  getOrRestoreSessionTicket(String authType, Map<String,String> parameters)
                                                  throws
                                                   RedirectException,
                                                   AuthException
    {
      HttpSession session = request_.getSession(true);
      Object o = session.getAttribute("lastUserTicket");
      if (o!=null && o instanceof String) {
        return (String)o;
      } else {
        return getSessionTicket(authType,parameters);
      }    
    }
  



    @Permission(name="*")
    @Override
    public ClientUserInfo getUserInfo(String sessionTicket) throws InvalidSessionTicketException, AuthException {
        UserRecord ur = getUserRecord(sessionTicket);
        ClientUserInfo retval = new ClientUserInfo();
        if (copyUserAttributes(retval,ur)) {
            request_.getSession().setAttribute(sessionTicket, ur);
        }
        return retval;
    }

    @Override
    @Permission(name="*")
    public boolean checkUserPermission(String sessionTicket, String permission,
                                   Map<String,String> params)
                                            throws InvalidSessionTicketException,
                                                   AuthException
    {      
        UserRecord ur = getUserRecord(sessionTicket);       
        UserServerContext ctx = apiProvider_.findContextById(ur.getUserId());
        return ctx.checkPermission(permission, params);
    }

    @Override
    @Permission(name="*")
    public Map<String,Boolean> checkUserPermissions(String sessionTicket,
                                                    List<String> permissions)
                                            throws InvalidSessionTicketException,
                                                   AuthException
    {      
        UserRecord ur = getUserRecord(sessionTicket);
        UserServerContext ctx = apiProvider_.findContextById(ur.getUserId());
        return ctx.checkPermissions(permissions);
    }



    @Override
    @Permission(name="*")
    public void logout()
    {
      HttpSession session = request_.getSession(false);
      session.removeAttribute("lastUserId");
      session.removeAttribute("lastUserTicket");
    }

    
    private UserRecord getUserRecord(String sessionTicket) throws AuthException, InvalidSessionTicketException
    {
        HttpSession session = request_.getSession(false);
        if (session==null) {
            throw new AuthException("no session");
        }
        Object o = session.getAttribute(sessionTicket);
        if (o==null) {
            throw new InvalidSessionTicketException();
        }
        if (o instanceof UserRecord) {
            return (UserRecord)o;
        }else{
            throw new InvalidSessionTicketException();            
        }        
    }


    /**
     * copy user attributes.
     * return true, if user record is changed.
     */
    private boolean copyUserAttributes(ClientUserInfo cui, UserRecord ur) throws AuthException
    {
      boolean urChanged=false;
      String nickname=ur.getAttribute("nickname");
      UserServerContext ctx = null;
      if (nickname==null) {
          if (ctx==null) ctx=apiProvider_.findContextById(ur.getUserId());
          nickname=ctx.getProperty("nickname");
          if (nickname!=null) {
              ur.setAttribute("nickname", nickname);
              urChanged=true;
              cui.setNickname(nickname);
          }
      }else{
          cui.setNickname(nickname);
      }
      String email=ur.getAttribute("email");
      if (email==null){
          if (ctx==null) ctx=apiProvider_.findContextById(ur.getUserId());
          email=ctx.getProperty("email");
          if (email!=null) {
              ur.setAttribute("email", email);
              urChanged=true;
              cui.setEmail(email);
          }
      }else{
          cui.setEmail(email);
      }
      String fullname=ur.getAttribute("fullname");
      if (fullname==null) {
          if (ctx==null) ctx=apiProvider_.findContextById(ur.getUserId());
          fullname=ctx.getProperty("fullname");
          if (fullname!=null) {
             ur.setAttribute("fullname", fullname);
             urChanged=true;
             cui.setFullName(fullname);
          }
      }else{
          cui.setFullName(fullname);
      }
      String gender=ur.getAttribute("gender");
      if (gender==null) {
          if (ctx==null) ctx=apiProvider_.findContextById(ur.getUserId());
          gender=ctx.getProperty("gender");
          if (gender!=null) {
              ur.setAttribute("gender", gender);
              urChanged=true;
              cui.setGender(gender);
          }
      }else{
          cui.setGender(gender);
      }
      String language=ur.getAttribute("language");
      if (language==null) {
          if (ctx==null) ctx=apiProvider_.findContextById(ur.getUserId());
          language=ctx.getProperty("language");
          if (language==null) {
              ur.setAttribute("language", language);
              urChanged=true;
              cui.setLanguage(language);
          }
      }else{
          cui.setLanguage(language);
      }
      String country=ur.getAttribute("country");
      if (country==null) {
          if (ctx==null) ctx=apiProvider_.findContextById(ur.getUserId());
          language=ctx.getProperty("country");
          if (language==null) {
              ur.setAttribute("country", country);
              urChanged=true;
              cui.setCountry(country);
          }
      }else{
          cui.setCountry(country);
      }
      return urChanged;
    }
    
    
    @Resource
    public AuthServerApiProvider getApiProvider()
    { return apiProvider_; }

    public void setApiProvider(AuthServerApiProvider apiProvider)
    { apiProvider_ = apiProvider; }

    @Resource
    public HttpServletRequest getHttpServletRequest()
    { return request_; }

    public void setHttpServletRequest(HttpServletRequest request)
    { request_ = request; }

    @Resource
    public int getDebugLevel()
    { return debugLevel_; }
    
    public void setDebugLevel(int debugLevel)
    {
      debugLevel_=debugLevel;  
    }

    public AuthClientApiHttpRequestScopeImpl(AuthServerApiProvider apiProvider,
                             HttpServletRequest request)
    {
      apiProvider_=apiProvider;
      request_=request;
    }

    public static class UserRecord implements Serializable
    {
        public UserRecord(String userId)
        {
         userId_=userId;
         String msg = userId_+Long.toHexString(System.currentTimeMillis())+Integer.toHexString(random.nextInt());
         sessionTicket_=md5Hash(msg);
        }
        
        public String getUserId()
        { return userId_; }
        
        public String getSessionTicket()
        { return sessionTicket_; }
        
        public String getAttribute(String attrName)
        {
           lazyInitAttrs(); 
           return attrs_.get(attrName);
        }

        public void setAttribute(String attrName, String attrValue)
        {
           lazyInitAttrs();
           attrs_.put(attrName, attrValue);
        }

        private void lazyInitAttrs()
        {
          if (attrs_==null) {
              attrs_=new TreeMap<String,String>();
          }
        }

        String sessionTicket_;
        String userId_;
        TreeMap<String,String> attrs_;
    }


    private static String md5Hash(String msg)
    {
      try {
         MessageDigest digest = MessageDigest.getInstance("MD5");
         digest.update(msg.getBytes());
         byte[] b = digest.digest();
         StringBuilder sb = new StringBuilder();
         for(int i=0; i<b.length; ++i) {
             sb.append(Integer.toHexString(0xFF & b[i]));
         }
         return sb.toString();
      }catch(NoSuchAlgorithmException ex){
          // impossible.
          throw new IllegalStateException("Can't find md5 algorithm",ex);
      }
    }

    private AuthServerApiProvider apiProvider_;
    private HttpServletRequest request_;
    private int debugLevel_=0;

    private static Random random = new Random();
}
