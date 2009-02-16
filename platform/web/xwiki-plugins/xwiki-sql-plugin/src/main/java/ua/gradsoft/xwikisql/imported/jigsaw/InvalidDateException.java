// InvalidDateException.java
// $Id: InvalidDateException.java,v 1.1 2000/09/26 13:54:04 bmahe Exp $
// (c) COPYRIGHT MIT, INRIA and Keio, 2000.
// Please first read the full copyright statement in file DateParser.java
//
// (C) Ruslan Shevchenko, GradSoft LTD.
//
package ua.gradsoft.xwikisql.imported.jigsaw;

/**
 * Invalid parse
 */
public class InvalidDateException extends Exception {

    public InvalidDateException(String msg) {
	super(msg);
    }
    
}
