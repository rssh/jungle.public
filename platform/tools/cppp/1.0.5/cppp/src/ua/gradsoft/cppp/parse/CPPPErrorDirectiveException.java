/*
 * CPPPErrorDirectiveException.java
 *
 */

package ua.gradsoft.cppp.parse;

import ua.gradsoft.termware.TermWareException;

/**
 * Exception, which is throwed by CPPP preprocessor on error directive.
 * 
 */
public class CPPPErrorDirectiveException extends TermWareException {
    
    
    /**
     * Constructs an instance of <code>CPPPErrorDirectiveException</code> with the specified detail message.
     * @param msg the detail message.
     */
    public CPPPErrorDirectiveException(int line,String file,String restOfLine) {
        super("#error on line "+line+" file "+file+":"+restOfLine);
        line_=line;
        file_=file;
        restOfLine_=restOfLine;
    }
    
    
    /**
     * return line of #error directive
     **/
    public int getLine()
    { return line_; }
    
    /**
     * return file name of #error directive.
     **/
    public String getInputFname()
    { return file_; }
    
    /***
     * return message after #error.
     **/
    public String getDirectiveMessage()
    { return restOfLine_; }
    
    private int line_;    
    private String file_;    
    private String restOfLine_;

    
}
