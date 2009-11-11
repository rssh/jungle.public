package ua.gradsoft.cppp;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.InputStream;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.io.Reader;
import java.util.Properties;
import junit.framework.TestCase;



public class Cppp1Test extends TestCase
{

    
  public void doCppp(CPPPFacade facade, String inputRelFname,String outputRelFname) throws Exception
  {
      String basedir = System.getProperty("basedir");    
      //System.out.println("basedir is:"+basedir);      
      File inputFile = new File(basedir+"/testdata/input/"+inputRelFname);
      Reader reader = new BufferedReader(new FileReader(inputFile));
      facade.setInputReader(reader);
      facade.setInputFilename(inputFile.getAbsolutePath());        
      PrintWriter pfo = new PrintWriter(new FileWriter(basedir+"/testdata/output/"+outputRelFname));      
      facade.setOutputWriter(pfo);
      facade.run();      
      reader.close();
      pfo.close();      
  }
    
    
  public void testCppp1_ND() throws Exception
  {
      CPPPFacade facade = new CPPPFacade();
      String basedir = System.getProperty("basedir"); 
      doCppp(facade,"1.pp.in","1.pp");
      assertTrue("outp file must contains CCC",checkStringInFile("CCC",basedir+"/testdata/output/1.pp"));
      System.out.println("testCppp1_ND passed");
  }

  public void testCppp2_ND() throws Exception
  {
      CPPPFacade facade = new CPPPFacade();
      facade.getPredefined().put("DB_CONNECTION_URL", "jdbc:oracle:thin:127.0.0.1:1521:XE");
      facade.getPredefined().put("DB_LOGIN", "AAA");
      facade.getPredefined().put("DB_PASSWORD", "BBB");
     // facade.setDebug(true);
      String basedir = System.getProperty("basedir"); 
      doCppp(facade,"2.xml.in","2.xml");
      assertTrue("outp file must not contains DB_CONNECTION_URL",!checkStringInFile("DB_CONNECTION_URL",basedir+"/testdata/output/2.xml"));
      System.out.println("testCppp2_ND passed");
  }
  
  public void testCppp3_ND() throws Exception
  {
      CPPPFacade facade = new CPPPFacade();
      //facade.setDebug(true);
      doCppp(facade,"3.pp.in","3.pp");
      String basedir = System.getProperty("basedir"); 
      assertTrue("outp file must contains ll",checkStringInFile("ll",basedir+"/testdata/output/3.pp"));
      System.out.println("testCppp3_ND passed");
  }

  public void testCppp4_ND() throws Exception
  {
      CPPPFacade facade = new CPPPFacade();
      //facade.setDebug(true);
      facade.setEraseCppComments(false);
      doCppp(facade,"4.xml.in","4.xml");
      String basedir = System.getProperty("basedir"); 
      assertTrue("outp file must contains tx schemas",checkStringInFile(
         "http://www.springframework.org/schema/tx/spring-tx.xsd",
         basedir+"/testdata/output/4.xml"));
      System.out.println("testCppp4_ND passed");
  }

       
  public static boolean  checkStringInFile(String value, String fname) throws Exception
  {
      File inputFile = new File(fname);   
      BufferedReader reader = new BufferedReader(new FileReader(inputFile));
      boolean found=false;
      while(true){
          String s = reader.readLine();
          if (s==null) {
              break;
          }
          if (s.contains(value)) {
              found=true;
              break;
          }
      }
      return found;
  }

}
