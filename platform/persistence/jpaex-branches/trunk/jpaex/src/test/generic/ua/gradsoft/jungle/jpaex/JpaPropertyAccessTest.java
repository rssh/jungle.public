package ua.gradsoft.jungle.jpaex;


import java.util.List;
import org.junit.Assert;
import org.junit.Test;
import ua.gradsoft.jungle.jpaex.testent.Ent1;
import ua.gradsoft.jungle.persistence.jpaex.JpaEntityProperty;
import ua.gradsoft.jungle.persistence.jpaex.JpaEntityPropertyNotFoundException;
import ua.gradsoft.jungle.persistence.jpaex.JpaHelper;

public class JpaPropertyAccessTest
{

  @Test
  public void testFindPropertyByName() throws Exception
  {
    JpaEntityProperty<Ent1,String> p;
    p = JpaHelper.<Ent1,String>findJpaPropertyByName(Ent1.class, "name");

    Ent1 ent1 = new Ent1();
    ent1.setName("aaa");
    String s = p.getValue(ent1);
    Assert.assertEquals("aaa", s);
    p.setValue(ent1, "aaa-1");
    s = ent1.getName();
    Assert.assertEquals("aaa-1", s);
  }

  @Test
  public void testGetAllProperties() throws Exception
  {
      List<JpaEntityProperty> properties = JpaHelper.getAllJpaProperties(Ent1.class);
      Assert.assertTrue(properties.size()>3);
  }

  @Test
  public void testFindByColumnName() throws Exception
  {
     JpaEntityProperty<Ent1,String> vp = JpaEntityProperty.findByColumnName(Ent1.class, "V");
     Assert.assertEquals(vp.getName(), "value");
     Ent1 ent1 = new Ent1();
     ent1.setName("name");
     ent1.setValue("value");
     vp.setValue(ent1, "xxx");
     Assert.assertEquals("xxx", vp.getValue(ent1));
  }

  @Test
  public void testFindUnexistentByColumnName() throws Exception
  {
   boolean wasCatched = false;
   try {
     JpaEntityProperty<Ent1,String> vp = JpaEntityProperty.findByColumnName(Ent1.class, "UNEXISTENT");
   }catch(JpaEntityPropertyNotFoundException ex){
       wasCatched=true;
   }
   Assert.assertTrue(wasCatched);
  }


}
