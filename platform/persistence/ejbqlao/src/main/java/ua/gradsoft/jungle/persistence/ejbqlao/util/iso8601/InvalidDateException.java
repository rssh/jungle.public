// InvalidDateException.java
// (c) COPYRIGHT MIT, INRIA and Keio, 2000.
// Please first read the full copyright statement in file DateParser.java
//
// (C) Ruslan Shevchenko, GradSoft LTD.
//
package ua.gradsoft.jungle.persistence.ejbqlao.util.iso8601;

/**
 * Invalid parse
 */
public class InvalidDateException extends Exception {

    public InvalidDateException(String msg) {
	super(msg);
    }
    
}
