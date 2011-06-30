/*
 * CPPPFacade.java
 *
 */

package ua.gradsoft.cppp;

import java.io.InputStream;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.io.Reader;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import ua.gradsoft.cppp.parse.CPPPParser;
import ua.gradsoft.termware.NILTerm;
import ua.gradsoft.termware.TermWareException;

/**
 *Facade class for call of C preprocessor from Java.
 * @author rssh
 */
public class CPPPFacade {
    
    
    public Reader getInputReader()
    {
        return inputReader_;
    }
    
    public void  setInputReader(Reader inputReader)
    { 
      inputReader_=inputReader;  
    }
    
    public String getInputFilename()
    {
        return inputFilename_;
    }
    
    public void  setInputFilename(String inputFilename)
    {
        inputFilename_=inputFilename;
    }

    public PrintWriter  getOutputWriter()
    {
        return outputWriter_;
    }
    
    
    public void  setOutputWriter(PrintWriter outputWriter)
    {     
        outputWriter_=outputWriter;
    }
    
public boolean  isPrintFileAndLine()
 {
  return printFileAndLine_;
 }

 public void    setPrintFileAndLine(boolean v)
 {
  printFileAndLine_=v; 
 }

 public String getOutputLinePrefix()
 {
     return outputLinePrefix_;
 }
 
 public void  setOutputLinePrefix(String outputLinePrefix)
 {
  outputLinePrefix_=outputLinePrefix;
 }    
 
 public Map<String,String>  getPredefined()
 {
   return predefined_;  
 }
 
 void setPredefined(Map<String,String> predefined)
 {
    predefined_=predefined; 
 }
 
 public List<String>  getIncludesSearchPath()
 {
     return includesSearchPath_;
 }
 
 public void  setIncludesSearchPath(List<String> includesSearchPath)
 {
     includesSearchPath_ = includesSearchPath;
 }
 
 public boolean getDebug()
 {
     return debug_;
 }
 
 public void setDebug(boolean debug)
 {
     debug_=debug;
 }
 
 public boolean getEraseCComments()
 { return eraseCComments_; }

 public void setEraseCComments(boolean value)
 { eraseCComments_=value; }

 public boolean getEraseCppComments()
 { return eraseCppComments_; }

 public void setEraseCppComments(boolean value)
 { eraseCppComments_=value; }

 public boolean getEraseXmlComments()
 { return eraseXmlComments_; }

 public void setEraseXmlComments(boolean value)
 { eraseXmlComments_=value; }

 public void run() throws TermWareException
 {
     CPPPParser parser = CPPPParser.create(inputReader_, inputFilename_,NILTerm.getNILTerm());
     parser.setDebug(debug_);
     parser.setEraseCComments(eraseCComments_);
     parser.setEraseCppComments(eraseCppComments_);
     parser.setEraseXmlComments(eraseXmlComments_);
     parser.setOutput(outputWriter_);
     parser.setOutputLinePrefix(outputLinePrefix_);
     parser.setPrintFileAndLine(printFileAndLine_);
     parser.setPredefined(predefined_);
     parser.setSearchPath(includesSearchPath_);
     parser.run();
 }
 
 
    private Reader      inputReader_ = null;
    private String      inputFilename_ = "unknown";
    
    private PrintWriter  outputWriter_ = null;
   
    private boolean     printFileAndLine_ = false;
    private String      outputLinePrefix_="#__FILE__:__LINE__:";
    private boolean     debug_=false;
    private boolean     eraseCComments_=true;
    private boolean     eraseCppComments_=true;
    private boolean     eraseXmlComments_=true;
    
    private Map<String,String>  predefined_ = new HashMap<String,String>();
    
    private List<String>        includesSearchPath_ = new LinkedList<String>();
    
}
