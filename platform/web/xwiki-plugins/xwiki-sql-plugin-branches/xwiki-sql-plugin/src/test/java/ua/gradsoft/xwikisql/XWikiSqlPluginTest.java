/*
 */

package ua.gradsoft.xwikisql;

import com.xpn.xwiki.XWikiContext;
import junit.framework.JUnit4TestAdapter;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 * @author rssh
 */
public class XWikiSqlPluginTest {

 // make ant happy
  public static junit.framework.Test suite(){
   return new JUnit4TestAdapter(XWikiSqlPluginTest.class);
 }

 @Test
 public void testCreateDB() throws Exception
 {
     XWikiContext ctx = new XWikiContext();
     SqlPlugin plugin = new SqlPlugin("sql", SqlPlugin.class.getName(),ctx);     
     plugin.init(ctx);
     SqlPluginApi api = new SqlPluginApi(plugin,ctx);
     
     SqlDatabase db = plugin.createDatabase(api,"TestDS");
  
 }
 
 @Test
 public void testExecuteNoVars() throws Exception
 {
     XWikiContext ctx = new XWikiContext();
     SqlPlugin plugin = new SqlPlugin("sql", SqlPlugin.class.getName(), ctx);
     plugin.init(ctx);
     SqlPluginApi api = new SqlPluginApi(plugin,ctx);
     
     SqlDatabase db = plugin.createDatabase(api,"TestDS");
     
     String createTableSql = 
             "create table test_table ( "
            +"  id bigint primary key, "
            +"  value varchar(100) "
            +")";
     
     
     db.executeUpdate(createTableSql);
  
     String insertSql1 = "insert into test_table(id,value) values(1,'aaa')";
     String insertSql2 = "insert into test_table(id,value) values(2,'bbb')";
     
     db.executeUpdate(insertSql1);
     db.executeUpdate(insertSql2);
     
     String selectSql = "select * from test_table";
     
     SqlResult result = db.executeQuery(selectSql);
     
     Assert.assertTrue("2 rows was inserted", result.getNRows()==2);
     Assert.assertTrue("table has 2 columns", result.getNColumns()==2);
     
     Assert.assertTrue("exists id column", result.hasColumnName("id"));
     Assert.assertTrue("exists value column", result.hasColumnName("value"));

     int nRetrievedRows=0;
     SqlResultRow rw=null;
     while(result.hasMoreElements()) {
         rw = result.nextElement();
         String ids = rw.getString("id");
         String vls = rw.getString("value");
        // System.err.println("("+ids+","+vls+")");
         ++nRetrievedRows;
     }
     Assert.assertTrue("two raws must be in enumerator", nRetrievedRows==2);
     
 }
 
 @Test
 public void testExecuteQueryNull1() throws Exception
 {
     XWikiContext ctx = new XWikiContext();
     SqlPlugin plugin = new SqlPlugin("sql", SqlPlugin.class.getName(), ctx);
     plugin.init(ctx);
     SqlPluginApi api = new SqlPluginApi(plugin,ctx);
     SqlDatabase db = plugin.createDatabase(api,"TestDS");
     
     String createTableSql = 
             "create table test_table_1 ( "
            +"  id bigint primary key, "
            +"  value varchar(100) "
            +")";
     
     
     db.executeUpdate(createTableSql);
  
     String insertSql1 = "insert into test_table_1(id,value) values(1,null)";
     String insertSql2 = "insert into test_table_1(id,value) values(2,null)";
     
     db.executeUpdate(insertSql1);
     db.executeUpdate(insertSql2);
     
     String selectSql = "select * from test_table_1";
     
     SqlResult result = db.executeQuery(selectSql);
     
     Assert.assertTrue("2 rows was inserted", result.getNRows()==2);
     Assert.assertTrue("table has 2 columns", result.getNColumns()==2);
     
     Assert.assertTrue("exists id column", result.hasColumnName("id"));
     Assert.assertTrue("exists value column", result.hasColumnName("value"));

     int nRetrievedRows=0;
     SqlResultRow rw=null;
     while(result.hasMoreElements()) {
         rw = result.nextElement();
         String ids = rw.getString("id");
         String vls = rw.getString("value");
        // System.err.println("("+ids+","+vls+")");
         boolean vln = rw.isNull("value");
         Assert.assertTrue("all value rows must be nullls",vln);
         ++nRetrievedRows;
     }
     Assert.assertTrue("two raws must be in enumerator", nRetrievedRows==2);
     
 }
    
 @Test
 public void testSetThrowExceptions()
 {
     XWikiContext ctx = new XWikiContext();
     SqlPlugin plugin = new SqlPlugin("sql", SqlPlugin.class.getName(), ctx);
     plugin.init(ctx);
     SqlPluginApi api = new SqlPluginApi(plugin,ctx);     
     SqlDatabase db = api.getDatabase("TestDS");
     db.setLogEnabled(false);

     boolean throwedError=false;     
     try {                 
        db.executeUpdate("incorrect sql");
     }catch(SqlRuntimeException ex){
         throwedError=true;
     }
     Assert.assertTrue("error must be throwed",throwedError);

     throwedError=false;
     db = api.getDatabase("TestDS");
     db.setThrowExceptions(false);
     db.setLogEnabled(false);
     try {
         db.executeUpdate("incorrect sql");
     }catch(SqlRuntimeException ex){
         throwedError=true;
     }
     Assert.assertTrue("error must be not throwed",!throwedError);
     Assert.assertTrue("error must be logged",db.wasError());     
     
 }
    
}
