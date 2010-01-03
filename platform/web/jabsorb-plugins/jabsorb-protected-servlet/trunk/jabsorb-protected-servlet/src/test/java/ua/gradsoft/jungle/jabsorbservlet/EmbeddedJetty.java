
package ua.gradsoft.jungle.jabsorbservlet;

import org.eclipse.jetty.server.Connector;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.nio.SelectChannelConnector;
import org.eclipse.jetty.webapp.WebAppContext;



/**
 *Class which hold embedded jetty
 * @author rssh
 */
public class EmbeddedJetty {

    
    /**
     * init and start
     */
    public void start()
    {
       server_ = new Server();
       Connector connector = new SelectChannelConnector();
       connector.setPort(PORT);
       connector.setHost(HOST);
       server_.addConnector(connector);

       WebAppContext wac = new WebAppContext();
       wac.setContextPath("/");
       wac.setWar("etc");

       //ContextHandlerCollectioon

       server_.setHandler(wac);
       server_.setStopAtShutdown(true);

       try {
         server_.start();
       }catch(Exception ex){
           throw new RuntimeException(ex);
       }
    }

    public void startAndWait()
    {
        start();
        int nAttempts = 0;
        while(!server_.isRunning() && nAttempts < 10) {
          try {
            Thread.sleep(1000);
          }catch(InterruptedException ex){
              break;
          }
          ++nAttempts;
        }
        if (!server_.isRunning()) {
            throw new RuntimeException("Server is not in runnign state during 10 sec.");
        }
    }

    public void join()
    {
      try {
       server_.join();
      }catch(InterruptedException ex){
          /* do nothing */;
      }
    }

    public static void main(String[] args)
    {
      if (args.length!=1) {
          System.err.println("args.length!=1");
          usage();
      }
      if (args[0].equals("start")) {
          EmbeddedJetty jetty = new EmbeddedJetty();
          jetty.start();
          jetty.join();
          System.err.println("started");
      }else if (args[0].equals("stop")) {
          System.err.println("stop is not implemented yet");
      }else{
          System.err.print("ars[0] must be start or stop");
          usage();
      }
    }

    private static void usage()
    {
      System.err.println("usage: EmbeddedJetty start|stop");  
    }

    private Server server_=null;

    public static final int PORT = 8080;
    public static final String HOST = "127.0.0.1";
}
