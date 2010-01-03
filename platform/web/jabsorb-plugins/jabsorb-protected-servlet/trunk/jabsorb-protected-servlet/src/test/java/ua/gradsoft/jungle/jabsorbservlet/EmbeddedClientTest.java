
package ua.gradsoft.jungle.jabsorbservlet;

import java.util.HashMap;
import java.util.Map;
import org.jabsorb.client.Client;
import org.jabsorb.client.ClientError;
import org.jabsorb.client.HTTPSession;
import org.jabsorb.client.TransportRegistry;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import ua.gradsoft.jungle.auth.client.AuthClientApi;

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
       //System.err.print("received:"+x);
       Assert.assertTrue(x==10);
    }



    @Test
    public void testClient1DenyRequest() throws Exception
    {
     HTTPSession session = newHTTPSession("http://127.0.0.1:8080/JSON-RPC");
     Client client = new Client(session);
     ITest test = (ITest)client.openProxy("testBean", ITest.class);
     int x=0;
     try {
       x = test.getP2();
     }catch(ClientError ex){
       // fo now we wan't check HTTP or JSONErrorCode (this functionality is
       //   missed in jabsorb for now)
     }
     Assert.assertEquals(x, 0);
    }

    @Test
    public void testLoginAndAccessRequest() throws Exception
    {
     HTTPSession session = newHTTPSession("http://127.0.0.1:8080/JSON-RPC");
     Client client = new Client(session);
     AuthClientApi auth = (AuthClientApi)client.openProxy("auth", AuthClientApi.class);
     Map<String,String> authParams = new HashMap<String,String>();
     authParams.put("username", "a1");
     authParams.put("password", "a1");
     String st = auth.getSessionTicket("plain", authParams);
     ITest test = (ITest)client.openProxy("testBean", ITest.class);
     int x=0;
     x = test.getP2();
     Assert.assertEquals(x, 20);
     auth.logout();
    }

    @Test
    public void testHashMap1() throws Exception
    {
     HTTPSession session = newHTTPSession("http://127.0.0.1:8080/JSON-RPC");
     Client client = new Client(session);
     Map<String,String> params = new HashMap<String,String>();
     params.put("username", "a1");
     params.put("password", "a1");
     ITest test = (ITest)client.openProxy("testBean", ITest.class);
     int x=0;
     x = test.testMapCall(params);
     Assert.assertEquals(x, 2);
    }


    private HTTPSession newHTTPSession(String url)
    {
        TransportRegistry registry = new TransportRegistry();
        HTTPSession.register(registry);
        return (HTTPSession)registry.createSession(url);
    }

    private static EmbeddedJetty server_=null;
}
