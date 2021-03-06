package ua.gradsoft.jungle.auth.server;

import java.util.Map;
import ua.gradsoft.jungle.auth.client.AuthException;
import ua.gradsoft.jungle.auth.client.RedirectException;

/**
 * This interface must be implemented by auth provider
 **/
public interface AuthServerApiProvider
{


  public UserServerContext  findAuthenticatedUserContext(String authType,
                                                         Map<String,String> namedParams)
                                                  throws RedirectException,
                                                         AuthException;
                                                         

  public UserServerContext  findContextById(String userId)
                                              throws AuthException;


  public UserServerContext   getAnonimousContext()
                                              throws AuthException;

}
