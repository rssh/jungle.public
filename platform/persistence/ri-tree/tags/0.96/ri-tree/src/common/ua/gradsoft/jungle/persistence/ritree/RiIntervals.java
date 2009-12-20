
package ua.gradsoft.jungle.persistence.ritree;

import javax.persistence.EntityManager;
import ua.gradsoft.jungle.persistence.jpaex.JpaEx;

/**
 *Helper class for maintaining persistend list of all RiIntervals
 * @author rssh
 */
public class RiIntervals  {

    public static void persist(EntityManager em, RiInterval interval)
    {
      getInstance().persist(em, interval);
    }


    public static void remove(EntityManager em, RiInterval interval)
    {
      getInstance().remove(em, interval);
    }

    public static boolean exists(EntityManager em, RiInterval interval)
    {
      return getInstance().exists(em, interval);
    }


    private static IRiIntervals getInstance()
    {
       if (impl_==null) {
           JpaEx jpaEx = JpaEx.getInstance();
           if (jpaEx.getDialect().equals("hibernate")) {
             try {
               Class objClass = Class.forName("ua.gradsoft.hibernateplugin.ritree.HibernateRiTreeIntervals");
               Object o = objClass.newInstance();
               impl_=(IRiIntervals)o;
             }catch(ClassNotFoundException ex){
                 throw new IllegalStateException(ex);
             }catch(InstantiationException ex){
                 throw new IllegalStateException(ex);
             }catch(IllegalAccessException ex){
                 throw new IllegalStateException(ex);
             }
           }else{
              throw new IllegalStateException("Sorry, plugin for Jpa dialect:"+jpaEx.getDialect()+" is not implemented");
           }
       } 
       return impl_;
        
    }

    private static IRiIntervals impl_=null;


}
