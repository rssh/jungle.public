
package ua.gradsoft.jungle.jabsorbservlet;

import org.jabsorb.client.Client;
import org.jabsorb.client.HTTPSession;
import org.jabsorb.client.TransportRegistry;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 *
 * @author rssh
 */
public class EmbeddedClientTest {

    @Before
    public void initEmbeddedServer()
    {
      if (server_==null) {
        server_=new EmbeddedJetty();
        server_.startAndWait();
      }
    }

    @Test
    public void testClient1Request() throws Exception
    {
     Client client = new Client(newHTTPSession("http://127.0.0.1:8080/JSON-RPC"));
     //try {
       ITest test = (ITest)client.openProxy("testBean", ITest.class);
       // p1 mus be allowed.
       int x = test.getP1();
     //}
       System.err.print("received:"+x);
       Assert.assertTrue(true);
    }
    
    private HTTPSession newHTTPSession(String url)
    {
        TransportRegistry registry = new TransportRegistry();
        HTTPSession.register(registry);
        return (HTTPSession)registry.createSession(url);
    }

    private EmbeddedJetty server_=null;
}
