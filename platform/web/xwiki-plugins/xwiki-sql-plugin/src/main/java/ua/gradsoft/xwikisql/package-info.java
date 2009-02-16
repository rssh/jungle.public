/**
 * XWikiSQL - plugin which allows us to query foreigh relational databases
 *  from velocity pages.
 *
 *<p>
 * In gnerelal, process of using XWikiSqlPlugin can be described in next way:
 * <ul>
 *   <li> Data sources is configurated as JNDI entries in the same container, 
 *         where XWiki run. </li>
 *   <li>
 *        Call of $sqlPlugin.callDatabase(ijndi-name) will return database 
 *        access objecsst of type SqlDatabase.
 *   </li>
 *   <li>
 *        Using database access object we can query or update database from 
 *         xwiki pages.
 *   </li>
 * </ul>
 *</p>
 *<p>
 *Example of usage:
 *
 *<pre>
## connect to database.
#set  ($mydb=$xwiki.sql.getDabase('MyDS'))
## print top 10 records from our table.
&lt;table&gt;
#foreach($row in $mydb.executeQuery(
                    'select id, name 
                          from (select id, name, rownum from test_table) 
                          where rownum < 10'
              ))
 &lt;tr&gt;
  &lt;td&gt; $row.getLong('id') &lt;/td&gt;
  &lt;td&gt; $row.getString('name') &lt;/td&gt;
 &lt;/tr&gt;
#end
&lt;/table&gt;
 *</pre>
 *</p>
 *<p>
 * (C) GradSoft Ltd, Kiev, Ukraine. <br>
 * <a href="http://www.gradsoft.ua"> http://www.gradsoft.ua </a> <br>
 * </br>
 *License: GPL version 3
 *<p>
 *</p>
 **/
package ua.gradsoft.xwikisql;
