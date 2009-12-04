
package ua.gradsoft.jungle.localization;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 *Test for loading data
 * @author rssh
 */
public class LoadedDataTest {

    @Before
    public void setUp() throws Exception
    {
       TestInfrastructureSingleton.lazyInit();
    }


    @Test
    public void testCountryLoaded()
    {
      CountryLinfo cl = TestInfrastructureSingleton.getEntityManager().
                                find(CountryLinfo.class,"UA");
      Assert.assertTrue(cl!=null);
    }



}
