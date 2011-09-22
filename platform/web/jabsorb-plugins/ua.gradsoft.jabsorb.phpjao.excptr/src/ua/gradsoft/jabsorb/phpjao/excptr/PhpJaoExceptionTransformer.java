package ua.gradsoft.jabsorb.phpjao.excptr;

import java.io.CharArrayWriter;
import java.io.PrintWriter;

import org.jabsorb.serializer.MarshallException;
import org.jabsorb.serializer.Serializer;
import org.jabsorb.serializer.SerializerState;
import org.jabsorb.JSONRPCResult;
import org.jabsorb.JSONRPCBridge;
import org.jabsorb.ExceptionTransformer;
import org.json.JSONObject;
import org.json.JSONException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * ExceptionTransformer for work with PHPJAO.
 *
 *  note, that for correct work of this transformer
 *  StackTraceSerializer also must be set in current bridge.
 *  i.e. correct initialization must looks like:
 *  <pre>
 *    PhpJaoExceptionTransformer et = new PhpJaoExceptionTransformer();
 *    bridge.setExceptionTransformer(et);
 *    et.setBridge(bridge);
 *    et.addClassSerializer(new StackTraceSerializer());
 *  </pre>
 **/
public class PhpJaoExceptionTransformer implements ExceptionTransformer
{

  public Object transform(Throwable t)
  {
    // try to serialize our exception as bean:
    Object o=null;
    try {
      SerializerState state = new SerializerState();
      o=bridge.getSerializer().marshall(state,null,t,"exception");
    } catch (MarshallException ex) {
      log.info("exception during marshall exception ",ex);
    }
    if (o==null) {
        // Than as some
        JSONObject retval = new JSONObject();
        try {
          retval.put("className",t.getClass().getName());
          retval.put("code", JSONRPCResult.CODE_REMOTE_EXCEPTION);
          if (t.getMessage()!=null) {
            retval.put("message",t.getMessage());
          }
          CharArrayWriter caw = new CharArrayWriter();
          t.printStackTrace(new PrintWriter(caw));
          retval.put("stackTrace",caw.toString());
        } catch (JSONException ex) {
           log.info("exception for getting properties",ex);
        }
        o=retval;
    }
    return o;
  }

  public JSONRPCBridge getBridge()
  { return bridge; }

  public void setBridge(JSONRPCBridge bridge)
  { this.bridge = bridge; }

  private JSONRPCBridge bridge;

  private final static Logger log = LoggerFactory.getLogger(PhpJaoExceptionTransformer.class);
      

}
