package ua.gradsoft.jungle.persistence.jpaex;

import java.lang.reflect.InvocationTargetException;
import java.sql.Connection;
import java.sql.SQLException;
import org.hibernate.Session;
import org.hibernate.jdbc.Work;

/**
 *Executor of JDFC worl
 * @author rssh
 */
public class HibernateJdbcWorkExecutor implements JdbcWorkExecutor
{

    static class WorkAdapter implements Work
    {

        WorkAdapter(JdbcWork jw)
        { jw_=jw; ex_=null; }

        public void execute(Connection cn) throws SQLException {
          try {
            jw_.execute(cn);
          }catch(Exception ex){
              ex_=ex;
          }
        }

        public Exception getEx()
        { return ex_; }

        private Exception ex_;
        private JdbcWork jw_;
    }


    public void execute(JdbcWork work) throws InvocationTargetException
    {
       WorkAdapter w = new WorkAdapter(work);
       session_.doWork(w); 
       if (w.getEx()!=null) {
           throw new InvocationTargetException(w.getEx());
       }
    }

    private Session session_;
}
