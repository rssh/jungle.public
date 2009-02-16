package  ua.gradsoft.xwikisql;


import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import javax.naming.Binding;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingEnumeration;
import javax.sql.DataSource;
import junit.framework.JUnit4TestAdapter;
import org.junit.Assert;
import org.junit.Test;

/**
 * more test of infrastructure, then our plugin
 **/
public class JndiDataSourceTest {

 @Test 
 public void testInitialContext() throws Exception
 {
     
   Context ctx = new InitialContext();
   
   //printContext(null, ctx);
   
   //DataSource ds0 = (DataSource) ctx.lookup("/comp/env/TestDS");
   DataSource ds = (DataSource) ctx.lookup("java:comp/env/TestDS");
   Assert.assertTrue(ds!=null);
   
   Connection cn = ds.getConnection();
   
   Statement st = cn.createStatement();
   
   String createTableSql = "create table test_table ( "
                          +"id bigint primary key, "
                          +"value varchar(100) "
                          +")";
   
   st.executeUpdate(createTableSql);
   
   st.close();
   
   st = cn.createStatement();
   
   ResultSet rs = st.executeQuery("select * from test_table");
   
   int nRows = 0;
   while(rs.next()) {
       ++nRows;
   }
   
   rs.close();
   
   st.close();
   
   cn.close();
   
   Assert.assertTrue("test_table must not contains elements ",nRows==0);
   
 }

 // make ant happy
 public static junit.framework.Test suite(){
   return new JUnit4TestAdapter(JndiDataSourceTest.class);
 }
 
 
 private void printContext(String prefix, Context ctx) throws Exception
 {
   NamingEnumeration<Binding> ne = ctx.listBindings("");
   
   while(ne.hasMoreElements()) {
       Binding b = ne.nextElement();
       
       String s = b.getName();
       Object o = b.getObject();     
       String nextPrefix = (prefix==null) ? s : prefix+"/"+s;
       System.err.println("binding:("+nextPrefix+","+o.toString()+")");
       if (o instanceof Context) {
           printContext(nextPrefix,(Context)o);
       }
   }
     
 }
 
}
