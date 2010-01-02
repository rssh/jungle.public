
package ua.gradsoft.jungle.jabsorbservlet;

import org.jabsorb.client.HTTPSession;
import org.jabsorb.client.TransportRegistry;
import org.junit.Before;
import org.junit.Test;

/**
 *
 * @author rssh
 */
public class EmbeddedClientTest {

    @Before
    void initEmbeddedServer()
    {
      if (server_==null) {
        server_=new EmbeddedJetty();
        server_.start();
      }
    }

    @Test
    void testClient1Request()
    {
      //Client client = new Client()
    }
    
    private HTTPSession newHTTPSession(String url)
    {
        TransportRegistry registry = new TransportRegistry();
        HTTPSession.register(registry);
        return (HTTPSession)registry.createSession(url);
    }

    private EmbeddedJetty server_=null;
}
