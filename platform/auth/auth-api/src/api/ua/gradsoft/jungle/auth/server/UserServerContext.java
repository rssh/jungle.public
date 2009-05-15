package ua.gradsoft.jungle.auth.server;

import java.io.Serializable;

/**
 * Interface for accessing user information.
 *API provider must implement one.
 */
public interface UserServerContext extends Serializable
{

  /**
   * return id of 
   **/
  public String getId();

  /**
   * get provider-specifics client properties.
   *Set of supported property names described in
   * onpenid-simple-extension.
   **/
  public String getProperty(String propertyName);

  /**
   * check permission
   **/
  public boolean checkPermission(String permissingName, 
                                 String permissionArgument);

}
