
package ua.gradsoft.cppp;


import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Reader;
import org.apache.tools.ant.*;
import org.apache.tools.ant.types.*;
import ua.gradsoft.termware.TermWareException;


/**
 *
 * @author rssh
 */
public class CPPPAntTask extends Task
{

    public CPPPAntTask()
    {
        facade_=new CPPPFacade();
        inputFname_=null;
        outputFname_=null;
    }
    
    public void  setInput(String inputFname)
    {
        inputFname_=inputFname;
    }
    
    public void setOutput(String outputFname)
    {
        outputFname_=outputFname;
    }
    
    public void  addConfiguredInclude(DirSet dirSet)
    {
      dirSet.setProject(this.getProject());
      facade_.getIncludesSearchPath().add(dirSet.getDir(getProject()).getAbsolutePath());
    }

    public static class Define
    {
        private String name_;
        private String value_;

        public String getName()            { return name_; }
        public void setName(String name)   { name_=name; }

        public String getValue()           { return value_; }
        public void   setValue(String value) { value_=value; }

        public String toString()
        { return "(#define "+name_+" "+value_+")"; }
        
    }
    
    public void addConfiguredDefine(Define define)
    {
        facade_.getPredefined().put(define.getName(),define.getValue());
    }
    
    
    public void  setPrintFileAndLine(boolean printFileAndLine)
    {
        facade_.setPrintFileAndLine(printFileAndLine);
    }
    
    public void  setOutputLinePrefix(String outputLinePrefix)
    {
        facade_.setOutputLinePrefix(outputLinePrefix);
    }
        
    public void  setDebug(boolean debug)
    {
        facade_.setDebug(debug);
    }

    public void  setEraseCComments(boolean value)
    {
        facade_.setEraseCComments(value);
    }

    public void  setEraseCppComments(boolean value)
    {
        facade_.setEraseCppComments(value);
    }

    public void  setEraseXmlComments(boolean value)
    {
        facade_.setEraseXmlComments(value);
    }

    
    public void  execute() throws BuildException
    {
        Reader reader=null;
        PrintWriter writer=null;
        boolean done=false;
        try {
          if (inputFname_!=null) {           
              reader=new BufferedReader(new FileReader(inputFname_));
              facade_.setInputFilename(inputFname_);
              facade_.setInputReader(reader);
            
          }
          if (outputFname_!=null) {
            writer = new PrintWriter(new FileWriter(outputFname_));      
            facade_.setOutputWriter(writer);            
          }
          facade_.run();
          done=true;
        }catch(IOException ex){
            throw new BuildException(ex.getMessage(),ex);
        }catch(TermWareException ex){
            throw new BuildException(ex);
        }finally{
           try{ 
            if (reader!=null) {
                reader.close();
            }
            if (writer!=null) {
                if (facade_.getDebug()) {
                    System.err.println("closing outputStream which is "+writer);
                }
                writer.flush();
                writer.close();
            }
           }catch(IOException ex){
              if (done) { 
                throw new BuildException(ex);
              }else{
                  // we are already in exception, do nothing to
                  //  handle previous exception.
              }
           }
        }
    }
    
    
    
    private CPPPFacade facade_;
    private String     inputFname_;
    private String     outputFname_;
    
}
