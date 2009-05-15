package ua.gradsoft.jungle.auth.client;

import java.io.Serializable;

/**
 * Set of standard users attributes,
 * (fields from openid-simple-registration-extension-1_0, not all).
 * Note, that any from this fields can be set to null.
 **/ 
public class ClientUserInfo implements Serializable
{

  /**
   * nickname of user is set.
   */
  public String getNickname()
  { return nickname_; }

  public void setNickname(String nickname)
  { nickname_=nickname; }
 
  /**
   * email of user, if known.
   **/
  public String getEmail()
  { return email_; }

  public void  setEmail(String email)
  { email_=email; }

  /**
   * Full name of user.
   **/ 
  public String getFullName()
  { return fullname_; }

  public void  setFullName(String fullname)
  { fullname_=fullname; }
  
  /**
   * ISO 3166 2-letter Country code
   **/
  public String getCountry()
  { return country_; }

  public void setCountry(String country)
  { country_=country; }

  /**
   * ISO 639 two-letter language code. 
   **/
  public String getLanguage()
  { return language_; }

  public void setLanguage(String language)
  {
    language_=language;
  }

  private String nickname_;
  private String email_;
  private String fullname_;
  private String country_;
  private String language_;

}
