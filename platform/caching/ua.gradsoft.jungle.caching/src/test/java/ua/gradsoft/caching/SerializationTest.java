package ua.gradsoft.caching;

import java.io.File;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.Unmarshaller;
import org.junit.Test;


public class SerializationTest
{
 
 @Test
 public void testReadConfiguration1() throws Exception
 {
   JAXBContext jc = JAXBContext.newInstance("ua.gradsoft.caching");
   Unmarshaller unmarshaller = jc.createUnmarshaller();
   Object o = unmarshaller.unmarshal(new File("testdata/test1.xml"));
 }

 @Test
 public void testReadConfiguration2() throws Exception
 {
   JAXBContext jc = JAXBContext.newInstance("ua.gradsoft.caching");
   Unmarshaller unmarshaller = jc.createUnmarshaller();
   Object o = unmarshaller.unmarshal(new File("testdata/test2.xml"));
 }



}
