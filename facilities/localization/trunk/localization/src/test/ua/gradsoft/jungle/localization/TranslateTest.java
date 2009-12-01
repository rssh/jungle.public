
package ua.gradsoft.jungle.localization;

import junit.framework.Assert;
import org.junit.Before;
import org.junit.Test;
import ua.gradsoft.jungle.localization.testdata.Country;

/**
 *Test to perform tanslation operation
 * @author rssh
 */
public class TranslateTest {

    @Before
    public void setUp() throws Exception
    {
       TestInfrastructureSingleton.lazyInit();
    }
   
    @Test
    public void testTranslateCountryBean()
    {
      Country cn = TestInfrastructureSingleton.getEntityManager().find(Country.class, "UA");
      assert(cn!=null);
      Assert.assertEquals("Ukraine", cn.getName());
      LocalizationFacade lf = TestInfrastructureSingleton.getLocalizationFacadeImpl();
      Country cn1 = lf.translateBean(cn, "uk", true);
      Assert.assertEquals("Україна", cn1.getName());
      // and original must not be translated.
      Country cn2 = TestInfrastructureSingleton.getEntityManager().find(Country.class, "UA");
      Assert.assertEquals("Ukraine", cn2.getName());
    }


}
