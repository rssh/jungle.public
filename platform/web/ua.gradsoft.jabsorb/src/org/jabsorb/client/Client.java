/*
 * jabsorb - a Java to JavaScript Advanced Object Request Broker
 * http://www.jabsorb.org
 *
 * Copyright 2007-2008 The jabsorb team
 *
 * based on original code from
 * JSON-RPC-Client, a Java client extension to JSON-RPC-Java
 * (C) Copyright CodeBistro 2007, Sasha Ovsankin <sasha at codebistro dot com>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */
package org.jabsorb.client;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Iterator;

import org.jabsorb.JSONRPCBridge;
import org.jabsorb.JSONRPCResult;
import org.jabsorb.JSONSerializer;
import org.jabsorb.serializer.FixUp;
import org.jabsorb.serializer.SerializerState;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * [START HERE] A factory to create proxies for access to remote Jabsorb services.
 */
public class Client implements InvocationHandler
{
  private static Logger log = LoggerFactory.getLogger(Client.class);

  private Session session;

  private JSONSerializer serializer;
  
  /**
   * Maintain a unique id for each message
   */
  private int id = 0;
  
  /**
   * Allow access to the serializer
   * 
   * @return The serializer for this class
   */
  public JSONSerializer getSerializer()
  {
    return serializer;
  }
  /**
   * Create a client given a session
   * 
   * @param session --
   *          transport session to use for this connection
   */
  public Client(Session session)
  {
    try
    {
      this.session = session;
      serializer = new JSONSerializer();
      serializer.registerDefaultSerializers();
    }
    catch (Exception e)
    {
      throw new ClientError(e);
    }
  }

  private synchronized int getId()
  {
    return id++;
  }

  /** Manual instantiation of HashMap<String, Object> */
  private static class ProxyMap extends HashMap
  {
    public String getString(Object key)
    {
      return (String) super.get(key);
    }

    public Object putString(String key, Object value)
    {
      return super.put(key, value);
    }
  }

  private ProxyMap proxyMap = new ProxyMap();

  /**
   * Create a proxy for communicating with the remote service.
   * 
   * @param key
   *          the remote object key
   * @param klass
   *          the class of the interface the remote object should adhere to
   * @return created proxy
   */
  public Object openProxy(String key, Class klass)
  {
    Object result = java.lang.reflect.Proxy.newProxyInstance(klass
        .getClassLoader(), new Class[] { klass }, this);
    proxyMap.put(result, key);
    return result;
  }

  /**
   * Dispose of the proxy that is no longer needed
   * 
   * @param proxy
   */
  public void closeProxy(Object proxy)
  {
    proxyMap.remove(proxy);
  }

  /**
   * This method is public because of the inheritance from the
   * InvokationHandler -- should never be called directly.
   */
  public Object invoke(Object proxyObj, Method method, Object[] args)
      throws Exception
  {
    String methodName = method.getName();
    if (methodName.equals("hashCode"))
    {
      return new Integer(System.identityHashCode(proxyObj));
    }
    else if (methodName.equals("equals"))
    {
      return (proxyObj == args[0] ? Boolean.TRUE : Boolean.FALSE);
    }
    else if (methodName.equals("toString"))
    {
      return proxyObj.getClass().getName() + '@'
          + Integer.toHexString(proxyObj.hashCode());
    }
    return invoke(proxyMap.getString(proxyObj), method.getName(), args, method.getReturnType());
  }

  private Object invoke(String objectTag, String methodName, Object[] args,
      Class returnType) throws Exception
  {
    final int id = getId();
    JSONObject message = new JSONObject();
    String methodTag = objectTag == null ? "" : objectTag + ".";
    methodTag += methodName;
    message.put("method", methodTag);

    {
      SerializerState state = new SerializerState();

      if (args != null)
      {

        JSONArray params = (JSONArray) serializer.marshall(state, /* parent */
        null, args, "params");

        if ((state.getFixUps() != null) && (state.getFixUps().size() > 0))
        {
          JSONArray fixups = new JSONArray();
          for (Iterator i = state.getFixUps().iterator(); i.hasNext();)
          {
            FixUp fixup = (FixUp) i.next();
            fixups.put(fixup.toJSONArray());
          }
          message.put("fixups", fixups);
        }
        message.put("params", params);
      }
      else
      {
        message.put("params", new JSONArray());
      }
    }
    message.put("id", id);

    JSONObject responseMessage = session.sendAndReceive(message);

    if (!responseMessage.has("result"))
    {
      processException(responseMessage);
    }
    Object rawResult = responseMessage.get("result");
    if (rawResult == null)
    {
      processException(responseMessage);
    }
    if (returnType.equals(Void.TYPE))
    {
      return null;
    }

    {
      JSONArray fixups = responseMessage.optJSONArray("fixups");

      if (fixups != null)
      {
        for (int i = 0; i < fixups.length(); i++)
        {
          JSONArray assignment = fixups.getJSONArray(i);
          JSONArray fixup = assignment.getJSONArray(0);
          JSONArray original = assignment.getJSONArray(1);
          JSONRPCBridge.applyFixup(rawResult, fixup, original);
        }
      }
    }
    return serializer.unmarshall(new SerializerState(), returnType, rawResult);
  }

  /**
   * Generate and throw exception based on the data in the 'responseMessage'
   */
  protected void processException(JSONObject responseMessage)
      throws JSONException
  {
    JSONObject error = (JSONObject) responseMessage.get("error");
    if (error != null)
    {
      Integer code = new Integer(error.has("code") ? error.getInt("code") : 0);
      String trace = error.has("trace") ? error.getString("trace") : null;
      String msg = error.has("msg") ? error.getString("msg") : null;
      throw new ErrorResponse(code, msg, trace);
    }
    else
      throw new ErrorResponse(new Integer(JSONRPCResult.CODE_ERR_PARSE),
          "Unknown response:" + responseMessage.toString(2), null);
  }

}
