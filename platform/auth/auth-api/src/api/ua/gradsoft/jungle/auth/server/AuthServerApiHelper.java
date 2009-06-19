
package ua.gradsoft.jungle.auth.server;

import java.lang.annotation.Annotation;
import java.lang.ref.SoftReference;
import java.lang.reflect.Method;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

/**
 *Utility class with helper methods.
 * @author rssh
 */
public class AuthServerApiHelper {

  /**
   * Check permisiion for method. (i. e. look on method annotations,
   *  and if method is annotated by &#064;Require or &#064;Permission
   *  in class or siuperclass or one of superinterfaces of declaring class,
   *  call check method in user server context).
   * @param method -
   * @param params
   * @param user
   * @return
   */
  public static boolean checkMethodPermissions(Method method,Object[] params,
                                               UserServerContext user)
  {
     Permission p = findAnnotation(method,Permission.class);
     if (p!=null) {
         return checkMethodPermission(method,params,user,p.name(),p.arguments());
     }
     Require r = findAnnotation(method,Require.class);
     if (r!=null) {
         for(Permission pr: r.permissions()) {
             if (checkMethodPermission(method,params,user,pr.name(),pr.arguments())) {
                 return true;
             }
         }
         return false;
     }
     // if we here - we have nothing. So
     return false;
  }

  private static boolean checkMethodPermission(Method method, Object[] params,
          UserServerContext user, String name,String[] arguments)
  {
      Map<String,String> mapargs = null;
      if (arguments!=null) {
          if ((arguments.length % 2) != 0) {
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


  /**
   * get security annotation for method.
   * Unlike method.getAnnotation() method, check not only in
   * supreclasses, but in interfaces.
   */
  private static <T extends Annotation> T  findAnnotation(Method m, Class<T> annotationClass)
  {
      Annotation retval = authAnnotationsHash_.get(m);

      if (retval==null) {
         retval=m.getAnnotation(annotationClass);
         if (retval==null) {
             Class cls = m.getDeclaringClass();         
             Class[] interfaces = cls.getInterfaces();             
             for(int i=0; i<interfaces.length; ++i) {               
                 try {
                   Method mi = interfaces[i].getMethod(m.getName(), m.getParameterTypes());
                   retval=mi.getAnnotation(annotationClass);
                 }catch(NoSuchMethodException ex){
                    continue;
                 }                 
                 if (retval!=null) {
                     break;
                 }

             }
         }
      }

      if (retval!=null) {
          authAnnotationsHash_.put(m, retval);
          return (T)retval;
      }else{
          return null;
      }
      
  }


  private static HashMap<Method,Annotation> authAnnotationsHash_ = new HashMap<Method,Annotation>();

}
