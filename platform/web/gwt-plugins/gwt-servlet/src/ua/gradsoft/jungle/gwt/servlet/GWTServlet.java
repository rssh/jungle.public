package ua.gradsoft.jungle.gwt.servlet;

import com.google.gwt.user.client.rpc.SerializationException;
import com.google.gwt.user.server.rpc.RPC;
import com.google.gwt.user.server.rpc.RPCRequest;
import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import java.io.Serializable;
import java.lang.reflect.Method;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NameNotFoundException;
import javax.naming.NamingException;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import net.sf.beanlib.hibernate.HibernateBeanReplicator;
import net.sf.beanlib.provider.replicator.BeanReplicator;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import ua.gradsoft.jungle.auth.client.UserIdParameter;
import ua.gradsoft.jungle.auth.server.AuthClientApiHttpRequestScopeImpl;
import ua.gradsoft.jungle.auth.server.AuthServerApiProvider;
import ua.gradsoft.jungle.auth.server.Permission;
import ua.gradsoft.jungle.auth.server.Require;
import ua.gradsoft.jungle.auth.server.RequirePermission;
import ua.gradsoft.jungle.auth.server.UserServerContext;

/**
 *<p>
 * Generic gwt servlet, configurable from JNDI or spring.
 *The work of Servlet is receive GWT call, determinate
 * object, which called (by JNDI name, same as pathInfo of
 * received HTTP request), do method call of server objects
 * (note, that server objects can not implement RemoteService),
 * and transform result back to client.
 *</p>
 * <p>
 * In addition, server can apply hooks for tranforming parameters before
 * method call and transforming result of call. This is particalury
 * useful for transforming JPA-implementations to plain POJO from internal
 * classes of JPA implementation.
 *</p>
 * <p>
 * Objects are located by JNDI name, by search in set of initial contenxs,
 * provided in GWTServlet configuration.  For example it is possible
 * setup GWTServlet at first search object in local spring context of web
 * application, than in external jndi context of ejb server.
 *</p>
 * <p>
 * If authApiProvider is set to configuration, gwtservlet also perform
 * interceptor call for access checking and implements auth client api.
 *@see ua.gradsoft.jungle.auth.server.AuthServerApiProvider
 *@see ua.gradsoft.jungle.auth.client.AuthClientApi
 *</p>
 **/ 
public class GWTServlet extends RemoteServiceServlet                       
{


  /**
   *Configuration for GWTServler consists from:
   * <ul>
   *  <li> set of parameters for setting JNDI names. </li>
   *  <li> classnames for input and output replicators </li>
   * <ul>
   * 
   * Set of parameters for setting JNDI names looks like:
   * <pre>
   *   prefix1.java.naming.factory.initial = first-naming-factory
   *   prefix1.java.naming.provider.url=first-provider
   *   prefix2.java.naming.factory.initial=second-naming-factory
   *   prefix2.java.naming.provider.url=second-provider
   *   .....
   *   java.naming.factory.initial=default-namig-factory
   * </pre>
   *(here x=y is used instead tags, of ciurse in WEB.xml it's writen
   * with standard &lt;init-param&gt; tags)
   *
   * GWTServlet will receive request in form of "/Context/Servlet/ObjectName"
   * and will search target objects in all configured named contexts.
   *
   * Other options are:
   * <pre>
   *  gwtservlet.input.replicator.classname = classname
   *  gwtservlet.output.replicator.classname = classname
   * </pre>
   * Class must be beanlib replicator (see <href="http://beanlib.sourceforge.net/">
   *  http://beanlib.sourceforge.net/ </a>) with default constructor.
   *
   * <pre>
   *  spring=&lt;any&gt;
   * </pre>
   * is shortcut to
   * <pre>
   * spring.java.naming.factory.initial = org.apache.xbean.spring.jndi.SpringInitialContextFactory
   * </pre>
   *
   * <pre>
   *  authApiProvider=name   
   * </pre>
   *  set name of bean for authApiProvider
   *
   * <pre>
   *  clientSideAuthName=name
   * </pre>
   *  set 'name' of client-side auth object, i, e.
   *
   * <pre>
   *  debug = 1 or 0
   * </pre>
   *  enable or disable debug.
   *
   *
   * @param servletConfig
   */
  @Override
  public void init(ServletConfig servletConfig) throws ServletException
  {
      super.init(servletConfig);
      Map<String,Hashtable<String,String>> namedEnvs = new HashMap<String,Hashtable<String,String>>();
      Hashtable<String,String> defaultEnv = new Hashtable<String,String>();
      Enumeration e = servletConfig.getInitParameterNames();
      String outputReplicatorClassname = null;
      String inputReplicatorClassname = null;
      boolean noDefaultJndi = false;
      while(e.hasMoreElements()) {
          String name = (String)e.nextElement();
          String value = servletConfig.getInitParameter(name);
          boolean b = checkNamedProperty(name,Context.INITIAL_CONTEXT_FACTORY,
                        namedEnvs,defaultEnv,value);
          if (!b) {
              b = checkNamedProperty(name,Context.PROVIDER_URL,namedEnvs,defaultEnv,value);
          }
          if (!b) {
              if (name.equals("gwtservlet.output.replicator.classname")) {
                  outputReplicatorClassname = servletConfig.getInitParameter(name);
                  b=true;
              }
          }
          if (!b) {
              if (name.equals("gwtservlet.input.replicator.classname")) {
                  inputReplicatorClassname = servletConfig.getInitParameter(name);
                  b=true;
              }
          }
          if (!b) {
              if (name.equals("spring")) {
                  checkNamedProperty("spring."+Context.INITIAL_CONTEXT_FACTORY,
                          Context.INITIAL_CONTEXT_FACTORY,namedEnvs,defaultEnv,
                          "org.apache.xbean.spring.jndi.SpringInitialContextFactory");
                  b=true;
              }
          }
          if (!b) {
              if (name.equals("debug")) {
                  if (value.equals("1")||value.equalsIgnoreCase("true")) {
                      debug_=true;
                  }else if (value.equals("0")||value.equalsIgnoreCase("false")) {
                      debug_=false;
                  }else{
                      Log log = LogFactory.getLog(GWTServlet.class);
                      log.warn("invalud value of debug parameter, must be 0 or 1; set to true");
                      debug_=true;
                  }
              }
          }
          if (!b) {
              if (name.equals("authApiProvider")) {
                  authApiProviderName_=servletConfig.getInitParameter("authApiProvider");
                  b=true;
              }
          }
          if (!b) {
              if (name.equals("clientSideAuthName")) {
                  clientSideAuthName_=servletConfig.getInitParameter("clientSideAuthName");
                  b=true;
              }
          }
          if (!b) {
              int pos = name.indexOf(".");
              if (pos==-1) {
                Log log = LogFactory.getLog(GWTServlet.class);
                log.warn("unknown init parameter "+name);
              }
              //String prefix = name.substring(0,pos);
              String rest = name.substring(pos+1);
              if (rest.startsWith("java")) {
                  b=checkNamedProperty(name,rest,namedEnvs,defaultEnv,
                                       servletConfig.getInitParameter(name));
                  if (!b) {
                     // impossible.
                     Log log = LogFactory.getLog(GWTServlet.class);
                     log.warn("internal error durting handling init parameter "+name);
                  }
              }else{
                Log log = LogFactory.getLog(GWTServlet.class);
                log.warn("unknown init parameter "+name);
              }
          }
      }
      if (!namedEnvs.isEmpty()) {
          for(Map.Entry<String,Hashtable<String,String>> je: namedEnvs.entrySet()) {
              try {
                Context ctx = new InitialContext(je.getValue());
                if (contexts_==null) {
                  // bust be initialized in costructor, but .. (?)
                  contexts_=new LinkedList<Context>();
                  Log log = LogFactory.getLog(GWTServlet.class);
                  log.warn("servlet context field was not initialized, check version of web container");
                }
                contexts_.add(ctx);
              }catch(NamingException ex){
                  Log log = LogFactory.getLog(GWTServlet.class);
                  log.error("Can't init name service for "+je.getKey(), ex);
                  throw new ServletException("Can't init name service for "+je.getKey(),ex);
              }
          }
      }
      if (!noDefaultJndi) {
         try {
           Context ctx=( defaultEnv.isEmpty() ? new InitialContext() :
                                                new InitialContext(defaultEnv));
           contexts_.add(ctx);
         }catch(NamingException ex){
           Log log = LogFactory.getLog(GWTServlet.class);
           log.error("Can't init default name service", ex);
           throw new ServletException("Can't init default name service",ex);
         }
      }

      if (outputReplicatorClassname!=null) {
          try {
            Class<?> outputReplicatorClass = Class.forName(outputReplicatorClassname);
            Object o = outputReplicatorClass.newInstance();
            if (o instanceof BeanReplicator) {
               resultReplicator_ = (BeanReplicator)o;
            }else if (o instanceof HibernateBeanReplicator){
               resultHibernateBeanReplicator_ = (HibernateBeanReplicator)o;
            }else{
              Log log = LogFactory.getLog(GWTServlet.class);
              log.fatal("output replicator class is not BeanReplicator or HibernateBeanReplicator");
              throw new ServletException("output replicator "+o.getClass()+" is not BeanReplicator or HibernateBeanReplicator");
            }
          }catch(ClassNotFoundException ex){
              Log log = LogFactory.getLog(GWTServlet.class);
              log.fatal("output replicator class not found", ex);
              throw new ServletException("output replicator class not found", ex);
          }catch(InstantiationException ex){
              Log log = LogFactory.getLog(GWTServlet.class);
              log.fatal("Can't instantiate output replicator",ex);
              throw new ServletException("Can't instantiate output replicator",ex);
          }catch(IllegalAccessException ex){
              Log log = LogFactory.getLog(GWTServlet.class);
              log.fatal("Can't instantiate output replicator",ex);
              throw new ServletException("Can't instantiate output replicator",ex);
          }
      }

      if (inputReplicatorClassname!=null) {
          try {
            Class<?> inputReplicatorClass = Class.forName(inputReplicatorClassname);
            inputParametersReplicator_ = (BeanReplicator)inputReplicatorClass.newInstance();
          }catch(ClassNotFoundException ex){
              Log log = LogFactory.getLog(GWTServlet.class);
              log.fatal("input replicator class not found", ex);
              throw new ServletException("input replicator class not found", ex);
          }catch(InstantiationException ex){
              Log log = LogFactory.getLog(GWTServlet.class);
              log.fatal("Can't instantiate input replicator",ex);
              throw new ServletException("Can't instantiate input replicator",ex);
          }catch(IllegalAccessException ex){
              Log log = LogFactory.getLog(GWTServlet.class);
              log.fatal("Can't instantiate input replicator",ex);
              throw new ServletException("Can't instantiate input replicator",ex);
          }
      }

      if (authApiProviderName_!=null) {
          // here, becouse whe know, that naing is initialized
          for(Context context:contexts_) {
            try {
                Object o = context.lookup(authApiProviderName_);
                if (o instanceof AuthServerApiProvider) {
                    authApiProvider_ = (AuthServerApiProvider)o;
                    break;
                }else{
                    throw new ServletException("object with name "+authApiProviderName_+" not implement AuthServerApiProvider");
                }
            }catch(NameNotFoundException ex){
                /* do nothing */
            }catch(NamingException ex){
                throw new ServletException("Can't locate api provider:"+authApiProviderName_,ex);
            }
          }
          if (authApiProvider_==null) {
              throw new ServletException("AuthApiProivider with name "+authApiProviderName_+" is not found");
          }

          if (clientSideAuthName_==null) {
              clientSideAuthName_="auth";
          }
      }

  }



  @Override
  public String processCall(String payload) throws SerializationException
  {
    String responsePayload = null;
    RPCRequest rpcRequest = null;
    Object targetObject = null;
    if (responsePayload==null) {
      try {
        rpcRequest = RPC.decodeRequest(payload, null, this);
      } catch (NullPointerException ex) {
        Log LOG = LogFactory.getLog(GWTServlet.class);
        LOG.info("empty rpc request");
        throw new RuntimeException("Empty rpc request");
      }
    }
    Object retval=null;
    if (responsePayload == null) {
     try {
        targetObject = retrieveTargetObject(this.getThreadLocalRequest());
        if (targetObject == null) {
           Log log = LogFactory.getLog(GWTServlet.class);
           log.info("can't retrieve target object");
           throw new RuntimeException("target object not found");
        }
        Method method = rpcRequest.getMethod();
        Object[] params = rpcRequest.getParameters();
        Method targetMethod = findTargetMethod(targetObject, method , params);
        Class<?> methodParameterTypes[] = method.getParameterTypes();
        Class<?> targetMethodParameterTypes[] = targetMethod.getParameterTypes();
        UserServerContext userContext=null;
        if (authApiProvider_!=null) {
            HttpSession session = getThreadLocalRequest().getSession(false);
            Object o = session.getAttribute("lastUserId");
            if (o==null) {
                userContext = authApiProvider_.getAnonimousContext();
            } else {
                if (o instanceof UserServerContext) {
                    userContext = (UserServerContext)o;
                } else if (o instanceof String) {
                    userContext = authApiProvider_.findContextById((String)o);
                } else {
                    throw new RuntimeException("Internal error: can't deteminate user");
                }
            }
        }
        Object[] targetParams;
        int copyOffset=0;
        boolean copy=false;
        boolean eraseIdParam=false;
        int userIdParamIndex=-1;
        UserIdParameter uidAnn = targetMethod.getAnnotation(UserIdParameter.class);
        if (uidAnn!=null) {
            userIdParamIndex=uidAnn.value();
        }
        if (targetMethodParameterTypes.length > methodParameterTypes.length) {
          targetParams=new Object[targetMethodParameterTypes.length];
          copy=true;
          copyOffset=1;
          if (authApiProvider_!=null) {
            targetParams[0]=userContext;
          } else {
            throw new RuntimeException("AuthApiProvider must be set for call of "+targetMethod.toString());
          }
        } else if (targetMethodParameterTypes.length == methodParameterTypes.length) {
          if (inputParametersReplicator_!=null) {
              targetParams = new Object[targetMethodParameterTypes.length];
              copy=true;
          }else{
              copy=false;
              targetParams=params;
          }
        } else if (targetMethodParameterTypes.length < methodParameterTypes.length) {
              targetParams = new Object[targetMethodParameterTypes.length];
              copy=true;
              eraseIdParam = true;
        } else {
            // impossible.
            throw new IllegalStateException("Impossible situation with params length before server call");
        }
        if (copy) {
          if (inputParametersReplicator_!=null) {
              for(int i=copyOffset; i<targetMethodParameterTypes.length; ++i) {
                  if (i-copyOffset==userIdParamIndex) {
                      if (!eraseIdParam) {
                          Object o = params[i-copyOffset];
                          if (o!=null) {
                            targetParams[i]=authApiProvider_.findContextById(o.toString());
                          }else{
                            targetParams[i]=authApiProvider_.getAnonimousContext();
                          }
                          continue;
                      }else{
                         --copyOffset;
                      }
                  }
                 targetParams[i]=inputParametersReplicator_.replicateBean(params[i-copyOffset]);
              }
          }else{
              for(int i=copyOffset; i<targetMethodParameterTypes.length; ++i) {
                  if (i-copyOffset==userIdParamIndex) {
                      if (!eraseIdParam) {
                          Object o = params[i-copyOffset];
                          if (o!=null) {
                              targetParams[i]=authApiProvider_.findContextById(o.toString());
                          }else{
                              targetParams[i]=authApiProvider_.getAnonimousContext();
                          }
                          continue;
                      }else{
                          --copyOffset;
                      }
                  }
                  targetParams[i]=params[i-copyOffset];
              }
          }
        }

        if (authApiProvider_!=null) {
          if (!checkMethodPermissions(targetMethod,targetParams, userContext)) {
             throw new RuntimeException("Access denied");
          }
        }

        retval = targetMethod.invoke(targetObject, targetParams);

     }catch(RuntimeException ex){
        Log log = LogFactory.getLog(GWTServlet.class);
        log.info("exception during call of server object",ex);
        responsePayload = RPC.encodeResponseForFailure(null, ex, rpcRequest.getSerializationPolicy());
     }catch(Exception ex){
        // it's not runtime exception, so log only if debug is enabled or
        //exception can't be passed to client.
        boolean toLog= ((!(ex instanceof Serializable))||debug_);
        if (toLog) {
          Log log = LogFactory.getLog(GWTServlet.class);
          log.info("exception during call of server object",ex);
        }
        responsePayload = RPC.encodeResponseForFailure(null, ex, rpcRequest.getSerializationPolicy());
     }
    }
    if (responsePayload==null) {
      if (resultReplicator_!=null) {
          retval = resultReplicator_.replicateBean(retval);
      }else if(resultHibernateBeanReplicator_!=null){
          retval = resultHibernateBeanReplicator_.deepCopy(retval);
      }
      try {
        responsePayload = RPC.encodeResponseForSuccess(rpcRequest.getMethod(),
                                                       retval,
                                                       rpcRequest.getSerializationPolicy());
      }catch(IllegalArgumentException ex){
        Log log = LogFactory.getLog(GWTServlet.class);
        log.error("error diring ecoding server response",ex);
        responsePayload = RPC.encodeResponseForFailure(rpcRequest.getMethod(), ex);
      }
    }
    return responsePayload;
  }

  /**
   *
   * @param request
   * @return
   */
  private Object retrieveTargetObject(HttpServletRequest request)
  {
     String objectName = request.getPathInfo();
     if (objectName.startsWith("/")) {
         objectName=objectName.substring(1);
     }
     if (debug_) {
       Log log = LogFactory.getLog(GWTServlet.class);
       log.info("retrieveTarget object for name "+objectName);
     }
     if (clientSideAuthName_!=null) {
         if (objectName.equals(clientSideAuthName_)) {
             return new AuthClientApiHttpRequestScopeImpl(authApiProvider_,request);
         }
     }

     Object retval = null;
     for(Context context: contexts_) {
         try {
             if (debug_) {
               Log log = LogFactory.getLog(GWTServlet.class);
               log.info("search in "+context.toString());
             }
             retval = context.lookup(objectName);
         }catch(NameNotFoundException ex){
             if (debug_) {
                 Log log = LogFactory.getLog(GWTServlet.class);
                 log.info("Not found.");
             }
             ;
         }catch(NamingException ex){
             Log log = LogFactory.getLog(GWTServlet.class);
             log.error("error during server object lookup",ex);
         }
         if (retval!=null) {
             break;
         }
     }
     if (retval==null) {
         throw new RuntimeException("Can't find object with name "+objectName);
     }
     return retval;
  }

  private Method  findTargetMethod(Object targetObject, Method originMethod, Object params)
  {
    Class<?> parameterTypes[] = originMethod.getParameterTypes();
    try {
        return targetObject.getClass().getMethod(originMethod.getName(), parameterTypes);
    } catch (NoSuchMethodException ex){
        // check method with UserServerContext with first parameters.
        Class<?> newParameterTypes[] = new Class<?>[parameterTypes.length+1];
        newParameterTypes[0]=UserServerContext.class;
        System.arraycopy(parameterTypes,0 , newParameterTypes, 1, parameterTypes.length);
        try {
            return targetObject.getClass().getMethod(originMethod.getName(), newParameterTypes);
        }catch(NoSuchMethodException ex1){
            ;// do noting: next try
        }
    }
    // no: may be we have methods with UserIdParam ?
    UserIdParameter ann = originMethod.getAnnotation(UserIdParameter.class);
    if (ann==null) {
        throw new RuntimeException("Can't find method for "+originMethod.getName());
    }
    // now try to find server method with userContext on paramIndex
    Class<?> newParameterTypes[] = new Class<?>[parameterTypes.length];
    for(int i=0; i<parameterTypes.length; ++i) {
        if (i==ann.value()) {
            newParameterTypes[i]=UserServerContext.class;
        }else{
            newParameterTypes[i]=parameterTypes[i];
        }
    }
    try {
        return targetObject.getClass().getMethod(originMethod.getName(), newParameterTypes);
    }catch(NoSuchMethodException ex1){
        /* nothing, next try */;
    }
    // now try to erase UserServerContext
    newParameterTypes = new Class<?>[parameterTypes.length-1];
    for(int i=0; i<parameterTypes.length; ++i) {
        if (i<ann.value()) {
            newParameterTypes[i]=parameterTypes[i];
        }else if (i==ann.value()) {
            /* do nothing */;
        }else{
            newParameterTypes[i-1]=parameterTypes[i];
        }
    }
    try {
        return targetObject.getClass().getMethod(originMethod.getName(), newParameterTypes);
    }catch(NoSuchMethodException ex){
        throw new RuntimeException("Can't find method for "+originMethod.getName());
    }
 }


  private boolean  checkNamedProperty(String paramName, String propertyName,
                                      Map<String,Hashtable<String,String>> namedEnvs,
                                      Hashtable<String,String> defaultEnv,
                                      String paramValue)
  {
     if (paramName.endsWith(propertyName)) {
       int prefixIndex = paramName.lastIndexOf(propertyName);
       if (prefixIndex == 0) {
          defaultEnv.put(propertyName, paramValue);
       }else{
          String prefix = paramName.substring(0, prefixIndex-1);
          Hashtable<String,String> namedEnv = namedEnvs.get(prefix);
          if (namedEnv==null) {
              namedEnv=new Hashtable<String,String>();
              namedEnvs.put(prefix, namedEnv);
          }
          namedEnv.put(propertyName, paramValue);
       }
       return true;
     }else{
       return false;
     }

  }

  private boolean       checkMethodPermissions(Method method,Object[] params,
                                               UserServerContext user)
  {
     RequirePermission rp = method.getAnnotation(RequirePermission.class);
     if (rp!=null) {
         return checkMethodPermission(method,params,user,rp.name(),rp.arguments());
     }
     Require r = method.getAnnotation(Require.class);
     if (r!=null) {
         for(Permission p: r.permissions()) {
             if (checkMethodPermission(method,params,user,p.name(),p.arguments())) {
                 return true;
             }
         }
         return false;
     }
     // if we here - we have nothing. So
     return false;
  }

  private boolean checkMethodPermission(Method method, Object[] params,
          UserServerContext user, String name,String[] arguments)          
  {
      Map<String,String> mapargs = null;
      if (arguments!=null) {
          if ((arguments.length % 2) == 0) {
              throw new IllegalArgumentException("length of arguments must be even");
          }
          mapargs = new TreeMap<String,String>();
          for(int i=0; i<arguments.length; i+=2) {
              String argname = arguments[i];
              String argvalue = arguments[i+1];
              if (argvalue.startsWith("$")) {
                  try {
                    int argNumber = Integer.parseInt(argvalue.substring(1));
                    argvalue = params[argNumber].toString();
                  }catch(NumberFormatException ex){
                    throw new IllegalArgumentException("Only references to parameters can start with '$'");  
                  }
              }
              mapargs.put(argname, argvalue);              
          }
      }else{
          mapargs=Collections.<String,String>emptyMap();
      }
      return user.checkPermission(name, mapargs);
  }

  private boolean       debug_    = false;
  private List<Context> contexts_ = new LinkedList<Context>();

  private BeanReplicator inputParametersReplicator_=null;
  private BeanReplicator resultReplicator_=null;
  private HibernateBeanReplicator resultHibernateBeanReplicator_=null;

  private String authApiProviderName_ = null;
  private AuthServerApiProvider authApiProvider_=null;
  private String clientSideAuthName_=null;

}
