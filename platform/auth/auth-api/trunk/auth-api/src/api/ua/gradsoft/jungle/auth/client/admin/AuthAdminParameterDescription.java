package ua.gradsoft.jungle.auth.client.admin;

import java.io.Serializable;

/**
 *Description of parameters, which must be
 * @author rssh
 */
public class AuthAdminParameterDescription implements Serializable
{

    /**
     * Name of parameter
     */
    public String getName()
    { return name_; }

    public void setName(String name)
    { name_=name; }

    private String name_;

    /**
     * true if this parameter is required.
     */
    public boolean isRequired()
    { return isRequired_; }

    public void setRequired(boolean isRequired)
    { isRequired_=isRequired; }

    private boolean  isRequired_;

    /**
     * true, if this parameter must be password field on form.
     */
    public boolean isPassword()
    {  return isPassword_; }

    public  void    setPassword(boolean isPassword)
    {  isPassword_=isPassword;  }

    private boolean isPassword_;

    /**
     * true, if this parameter mut be viewed as list.
     * (in such case list elemets are stored/passed to server,
     *  as set. separated by '|' symbols).
     */
    public boolean isList()
    {  return isList_; }

    public  void    setList(boolean isList)
    {  isList_=isList;  }

    private boolean isList_;


    /**
     * maximal length of parameter (0 if not set).
     */
    public int  getMaxLength()
    {  return maxLength_; }
    
    public  void    setMaxLength(int maxLength)
    {  maxLength_=maxLength;  }
    
    private int maxLength_;
    
    /**
     * minimal length of parameter (0 if not set).
     */
    public int  getMinLength()
    {  return minLength_; }
    
    public  void    setMinLength(int minLength)
    {  minLength_=minLength;  }
    
    private int minLength_;
    
    /**
     * regular expression, which must be checked on form validation if set.
     */
    public String  getRegexpr()
      { return regExpr_; }

    public void  setRegexpr(String regExpr)
      { regExpr_=regExpr; }

    private String regExpr_;

    /**
     * when field actually an enum. In such case regExpr contains set of choices.
     */
    public boolean  isEnum()
      { return isEnum_; }

    public void  setEnum(boolean isEnum)
      { isEnum_=isEnum; }

    private boolean isEnum_=false;

    /**
     * Label on form (if we want one be differ from name)
     */
    public String  getLabel()
      { return label_; }

    public void  setLabel(String label)
      { label_=label; }

    private String label_;


}
