
package ua.gradsoft.hibernateplugin.ritree;

import java.text.SimpleDateFormat;
import javax.persistence.EntityManager;
import javax.persistence.Query;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import ua.gradsoft.hibernateplugin.ritree.testdata.MySeries;
import ua.gradsoft.jungle.persistence.ritree.RiInterval;
import ua.gradsoft.jungle.persistence.ritree.RiIntervals;

/**
 *
 * @author rssh
 */
public class PgSqlBaseBeforeTest {

    @Before
    public void setUp() throws Exception
    {
        FrameWorkInitializer.lazyInit();
    }
    
    @Test
    public void testRiTreeBefore1() throws Exception
    {
        initDataSet1();
        MySeries ms = FrameWorkInitializer.getEntityManager().find(MySeries.class, 1);
        Assert.assertNotNull(ms);
    }


    void initDataSet(int number) throws Exception
    {
        if (currentDataSet!=number) {
            if (currentDataSet!=0) {
                clearDataSet();
            }
            switch(number) {
                case 1: initDataSet1();
                default:
                    throw new RuntimeException("Invalid dataset number: "+number);
            }
        }
    }

    /**
     * DataSet wich contains from one point.
     * @throws Exception
     */
    private void initDataSet1() throws Exception
    {
      EntityManager em = FrameWorkInitializer.getEntityManager();  
      SimpleDateFormat f = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
      RiInterval ri = new RiInterval(f.parse("1999-01-01 00:00:00"),
                                     f.parse("2000-01-01 00:00:00"));
      RiIntervals.persist(em, ri);
      MySeries s1 = new MySeries();
      s1.setId(1);
      s1.setDescription("first point");
      s1.setInterval(ri);
      s1.setValue(0.01);      
      em.persist(s1);
    }


    private void clearDataSet()
    {
      EntityManager em = FrameWorkInitializer.getEntityManager();
      Query q = em.createQuery("delete from MySeries");
      q.executeUpdate();
    }


    private int currentDataSet=0;

}
