package ua.gradsoft.jungle.http.server;

import java.io.IOException;
import java.util.Enumeration;
import java.util.Map;
import java.util.TreeMap;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletResponse;


/**
 * Simple filter, which restrict access by Ip. Set of ip-s configured in
 * filter parameters.
 * (configuration description see in javadoc for init method).
 */
public class IpFilter implements Filter
{

    /**
     * names of parametrs must be ip-prefixes, i. e. sequences of
     * components, separated by '.'.
     * values must be 'allow' or 'deny'.
     * 
     * More specifics prefixes have priority over less-specifics.
     * 
     * Here is example
     * <pre>
     *  <init-param>
     *   <param-name>192.168</param-name>
     *   <param-value>allow</param-name>
     *  </init-param>
     *  <init-param>
     *   <param-name>192.168.0</param-name>
     *   <param-value>deny</param>
     *  </init-param>
     * </pre>
     *
     * requests from net  192.168.0.x will be denied, from other net-s
     *  with prefix 192.168 - allowed, for all other - denied.
     *
     * Also it is possible to set access value for all net with param-name '*'
     * <pre>
     * <init-param>
     *  <param-name>*</param-name>
     *  <param-valut>allow</param-value>
     * </init-param>
     * </pre>
     *  will allow requests from everywhere.
     *
     * @param filterConfig
     * @throws ServletException
     */
    public void init(FilterConfig filterConfig) throws ServletException {
        Enumeration en = filterConfig.getInitParameterNames();
        while(en.hasMoreElements()) {
            String paramName = (String)en.nextElement();
            String paramValue = filterConfig.getInitParameter(paramName);
            boolean b = false;
            if (paramValue.equalsIgnoreCase("allow")||paramValue.equals("1")) {
                b=true;
            } else if (paramValue.equalsIgnoreCase("deny")||paramValue.equalsIgnoreCase("0")) {
                b=false;
            } else {
                throw new ServletException("Invalid parameter value for "+paramName+": must be 'accept' or 'deny'");
            }
            paramName=paramName.replace("\\.\\*", "");
            String[] components = paramName.split("\\.");
            switch(components.length) {
                case 0:
                    /* impossible */
                    break;
                case 1:
                    if (components[0].equals("*")||components[0].equalsIgnoreCase("default")) {
                        acceptedByDefault_=b;
                    } else if (checkIpComponent(components[0])) {
                        if (accepted1_==null) {
                            accepted1_=new TreeMap<String,Boolean>();
                        }
                        accepted1_.put(paramName, b);
                    } else {
                        throw new ServletException("Bad IpFilter param "+paramName+", must be ip prefix");
                    }
                    break;
                case 2:
                    if (checkIpComponent(components[0])&& checkIpComponent(components[1])) {
                        if (accepted2_==null) {
                            accepted2_=new TreeMap<String,Boolean>();
                        }
                        accepted2_.put(paramName, b);
                    } else {
                        throw new ServletException("Bad IpFilter param "+paramName+", must be ip prefix");
                    }
                    break;
                case 3:
                    if (checkIpComponent(components[0])&&checkIpComponent(components[1])
                      &&checkIpComponent(components[2])) {
                        if (accepted3_==null) {
                            accepted3_=new TreeMap<String,Boolean>();
                        }
                        accepted3_.put(paramName, b);
                    } else {
                        throw new ServletException("Bad IpFilter param "+paramName+", must be ip prefix");
                    }
                    break;
                case 4:
                    if (checkIpComponent(components[0])&&checkIpComponent(components[1])
                       &&checkIpComponent(components[2])&&checkIpComponent(components[3])) {
                        if (accepted4_==null) {
                            accepted4_=new TreeMap<String,Boolean>();
                        }
                        accepted4_.put(paramName, b);
                    } else {
                        throw new ServletException("Bad IpFilter param "+paramName+", must be ip prefix");
                    }
                    break;
                default:
                    throw new ServletException("Bad IpFilter param "+paramName+", must be ip prefix");
            }
        }
    }



    public void doFilter(ServletRequest servletRequest, 
                         ServletResponse servletResponse, 
                         FilterChain filterChain) throws IOException, ServletException {
        String userIp = servletRequest.getRemoteAddr();
        if (checkIp(userIp)) {
            filterChain.doFilter(servletRequest, servletResponse);
        } else {
            ((HttpServletResponse)(servletResponse)).sendError(HttpServletResponse.SC_FORBIDDEN,
                    "not allowed");
        }
    }

    public void destroy() {
       /* do nothing */
    }
    
    private boolean checkIp(String ip)
    {
       if (accepted4_!=null) {
           Boolean b = accepted4_.get(ip);
           if (b!=null) {
               return b;
           }
       } 
       String[] components = ip.split("\\.");
       final String ip1 = components[0];
       final String ip2 = ip1+"."+components[1];
       final String ip3 = ip2+"."+components[2];
       if (accepted3_!=null) {
           Boolean b = accepted3_.get(ip3);
           if (b!=null) {
               return b;
           }
       }
       if (accepted2_!=null) {
           Boolean b = accepted2_.get(ip2);
           if (b!=null) {
               return b;
           }
       }
       if (accepted1_!=null) {
           Boolean b = accepted1_.get(ip1);
           if (b!=null) {
               return b;
           }
       }
       return acceptedByDefault_;
    }

    /**
     * check that argument is ip component.
     */
    private static boolean checkIpComponent(String component)
    {
       return component.matches("[0-9]+");
    }

    private Map<String,Boolean> accepted1_=null;
    private Map<String,Boolean> accepted2_=null;
    private Map<String,Boolean> accepted3_=null;
    private Map<String,Boolean> accepted4_=null;
    private boolean             acceptedByDefault_=false;


}
