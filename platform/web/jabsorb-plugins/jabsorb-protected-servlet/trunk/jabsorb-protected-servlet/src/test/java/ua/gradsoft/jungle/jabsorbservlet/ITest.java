
package ua.gradsoft.jungle.jabsorbservlet;

import java.util.Map;
import ua.gradsoft.jungle.auth.server.Permission;

/**
 *
 * @author rssh
 */
public interface ITest {

    @Permission(name="p1")
  public int getP1();

  @Permission(name="p2")
  public int getP2();

  @Permission(name="*")
  public int getP3();

  public int getP4();

  public int getP5(int x);

  @Permission(name="*")
  public int testMapCall(Map<String,String> arg);

}
