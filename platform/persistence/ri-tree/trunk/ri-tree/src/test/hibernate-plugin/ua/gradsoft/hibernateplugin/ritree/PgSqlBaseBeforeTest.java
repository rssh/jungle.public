
package ua.gradsoft.hibernateplugin.ritree;

import java.text.SimpleDateFormat;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.Query;
import org.hibernate.Session;
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
        initDataSet(1);
        MySeries ms = FrameWorkInitializer.getEntityManager().find(MySeries.class, 1);
        Assert.assertNotNull(ms);
    }

    @Test
    public void testRiTreeBefore2() throws Exception
    {
        initDataSet(1);
        EntityManager em = FrameWorkInitializer.getEntityManager();
        Session session = (Session)em.getDelegate();
        session.enableFilter("ri");
        SimpleDateFormat f = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        session.getEnabledFilter("ri").setParameter("bottom", f.parse("1999-01-01 00:00:00").getTime()/1000);
        session.getEnabledFilter("ri").setParameter("top", f.parse("1999-01-01 00:00:00").getTime()/1000);
        
        String ejbql = "select m from MySeries m, RiBefore b "+
                       " where b.interval=m.interval ";
                       //" and b.bottom=? and b.top=?";
       // String ejbql = "select m from MySeries m, RiTreeInterval b where m.interval=b.pk";
        Query q = em.createQuery(ejbql);
        //q.setParameter(":Ri.bottom", f.parse("1999-01-01 00:00:00").getTime()/1000);
        //q.setParameter(":Ri.top", f.parse("1999-01-01 00:00:00").getTime()/1000);
        //q.setParameter(1, f.parse("1999-01-01 00:00:00").getTime()/1000);
        //q.setParameter(2, f.parse("1999-01-01 00:00:00").getTime()/1000);
        List l = q.getResultList();
        Assert.assertNotNull(l);
        Assert.assertEquals("first point must not be in before",0, l.size());

        session.getEnabledFilter("ri").setParameter("bottom", f.parse("2001-01-01 00:00:00").getTime()/1000);
        session.getEnabledFilter("ri").setParameter("top", f.parse("2001-01-01 00:00:00").getTime()/1000);

        q = em.createQuery(ejbql);
        l = q.getResultList();
        Assert.assertNotNull(l);
        Assert.assertTrue("interva must be before 2001",l.size()!=0);
    }


    @Test
    public void testRiTreeBefore3() throws Exception
    {
        initDataSet(1);
        EntityManager em = FrameWorkInitializer.getEntityManager();
        Session session = (Session)em.getDelegate();
        session.enableFilter("ri");
        SimpleDateFormat f = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        session.getEnabledFilter("ri").setParameter("bottom", f.parse("2001-01-01 00:00:00").getTime()/1000);
        session.getEnabledFilter("ri").setParameter("top", f.parse("2001-01-01 00:00:00").getTime()/1000);


        String ejbql = "select b from RiBefore b ";
        Query q = em.createQuery(ejbql);

        List l = q.getResultList();
        Assert.assertTrue("something must be selected",l.size()!=0);
    }
     


    void initDataSet(int number) throws Exception
    {
        if (currentDataSet!=number) {
            if (currentDataSet!=0) {
                clearDataSet();
            }
            switch(number) {
                case 1: initDataSet1();
                        currentDataSet=1;
                        break;
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
      em.getTransaction().begin();
      SimpleDateFormat f = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
      RiInterval ri = new RiInterval(f.parse("1999-01-01 00:00:00"),
                                     f.parse("2000-01-01 00:00:00"));
      System.err.println("sec1="+f.parse("1999-01-01 00:00:00").getTime()/1000);
      RiIntervals.persist(em, ri);
      em.flush();
      MySeries s1 = new MySeries();
      s1.setId(1);
      s1.setDescription("first point");
      s1.setInterval(ri);
      s1.setValue(0.01);      
      em.merge(s1);
      em.getTransaction().commit();
    }


    private void clearDataSet()
    {
      EntityManager em = FrameWorkInitializer.getEntityManager();
      Query q = em.createQuery("delete from MySeries");
      q.executeUpdate();
    }


    private int currentDataSet=0;

}
