
package ua.gradsoft.jungle.localization;

import java.util.Collection;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.Query;
import junit.framework.Assert;
import org.junit.Before;
import org.junit.Test;
import ua.gradsoft.jungle.localization.testdata.City;
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

    @Test
    public void testTranslateCityBeans()
    {
      EntityManager em = TestInfrastructureSingleton.getEntityManager();
      LocalizationFacade lf = TestInfrastructureSingleton.getLocalizationFacadeImpl();
      Query q = em.createQuery("select city from City city");
      List<City> cities = (List<City>)q.getResultList();
      Collection<City> translated = lf.translateBeans(cities, "uk", true);

      boolean ukUkraine = false;
      boolean ukKiev=false;
      for(City city:translated) {
          if (city.getName().equals("Київ")) {
              ukKiev=true;
          }
          if (city.getCountry().getName().equals("Україна")) {
              ukUkraine=true;
          }
          //System.err.println(city.getName()+", "+city.getCountry().getName());
      }
      Assert.assertTrue(ukKiev);
      Assert.assertTrue(ukUkraine);
    }

    @Test
    public void testTranslateCityWithText1()
    {
      EntityManager em = TestInfrastructureSingleton.getEntityManager();
      LocalizationFacade lf = TestInfrastructureSingleton.getLocalizationFacadeImpl();
      Query q = em.createQuery("select city from CityWithText city");
      List<City> cities = (List<City>)q.getResultList();
      Collection<City> translated = lf.translateBeans(cities, "uk", true);
    }

}
