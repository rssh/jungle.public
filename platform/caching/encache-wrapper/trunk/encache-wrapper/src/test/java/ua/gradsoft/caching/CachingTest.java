
package ua.gradsoft.caching;

import org.junit.Test;
import static org.junit.Assert.*;
import ua.gradsoft.caching.testclasses.CachedClass1;
import ua.gradsoft.caching.testclasses.CachedClass2;
import ua.gradsoft.caching.testclasses.InterfaceForTest1;
import ua.gradsoft.caching.testclasses.InterfaceForTest2;


/**
 *
 * @author rssh
 */
public class CachingTest {

    @Test
    public void testCachedWrapper1()
    {
      CachedClass1 cachedObject = new CachedClass1();
      InterfaceForTest1 ic = CachingWrapper.getInstance().createCached(
                                               InterfaceForTest1.class,
                                               cachedObject);
      int x1 = ic.getX(1);
      int x2 = ic.getX(1);
      assertTrue("result fo first and second calls must be equal",x1==x2);
      assertEquals("nCalls",1,ic.getNCalls());
    }

    @Test
    public void testCachedWrapper2()
    {
      CachedClass2 cachedObject = new CachedClass2();
      InterfaceForTest2 ic = CachingWrapper.getInstance().createCached(
                                                          InterfaceForTest2.class,
                                                          cachedObject
                                                       );
      String r1 = ic.getXS("aa", "bb");
      String r2 = ic.getXS("aa", "aa");
      String r3 = ic.getXS("aa", "bb");
      String r4 = ic.getXS("aa", "aa");

      assertEquals(r1,r3);
      assertEquals(r2,r4);
      assertNotSame(r1, r2);
      assertEquals(ic.getNCalls(),2);

    }


}
