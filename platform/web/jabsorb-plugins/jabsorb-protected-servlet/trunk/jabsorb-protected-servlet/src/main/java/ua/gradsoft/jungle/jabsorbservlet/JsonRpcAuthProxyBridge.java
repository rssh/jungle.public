
package ua.gradsoft.jungle.jabsorbservlet;

import javax.servlet.http.HttpServletRequest;
import org.jabsorb.ExceptionTransformer;
import org.jabsorb.JSONRPCBridge;
import org.jabsorb.JSONRPCResult;
import org.jabsorb.callback.CallbackController;
import org.jabsorb.callback.InvocationCallback;
import org.jabsorb.serializer.ClassHintTranslator;
import org.jabsorb.serializer.Serializer;
import org.json.JSONObject;

/**
 *ProxyBridge, which check user permissions and reject requests
 * or redirect one to origin bridge
 * @author rssh
 */
public class JsonRpcAuthProxyBridge extends JSONRPCBridge
{

    public JsonRpcAuthProxyBridge(JSONRPCBridge origin,AuthInvocationCallback callback)
    {
      origin_=origin;
      origin_.registerCallback(callback,HttpServletRequest.class);
      System.err.println("callback registered");
    }

    @Override
    public void addReference(Object o) {
       origin_.addReference(o);
    }

    /**
     *TODO: check
     * @param context
     * @param jsonReq
     * @return
     */
    @Override
    public JSONRPCResult call(Object[] context, JSONObject jsonReq) {
          return origin_.call(context, jsonReq);
    }

    @Override
    public synchronized void enableReferences() throws Exception {
        origin_.enableReferences();
    }

    @Override
    public CallbackController getCallbackController() {
        return origin_.getCallbackController();
    }

    @Override
    public Object getReference(int objectId) {
        return origin_.getReference(objectId);
    }

    @Override
    public boolean isCallableReference(Class clazz) {
        return origin_.isCallableReference(clazz);
    }

    @Override
    public boolean isReference(Class clazz) {
        return origin_.isReference(clazz);
    }

    @Override
    public Class lookupClass(String name) {
        return origin_.lookupClass(name);
    }

    @Override
    public Object lookupObject(Object key) {
        return origin_.lookupObject(key);
    }

    @Override
    public void registerCallableReference(Class clazz) throws Exception {
        origin_.registerCallableReference(clazz);
    }

    @Override
    public void registerCallback(InvocationCallback callback, Class contextInterface) {
        origin_.registerCallback(callback, contextInterface);
    }

    @Override
    public void registerClass(String name, Class clazz) throws Exception {
        origin_.registerClass(name, clazz);
    }

    @Override
    public void registerMarshallClassHint(Class internalClass, Class externalClass) {
        origin_.registerMarshallClassHint(internalClass, externalClass);
    }

    @Override
    public void registerMarshallClassHintTranslator(ClassHintTranslator translator) {
        origin_.registerMarshallClassHintTranslator(translator);
    }

    @Override
    public void registerObject(Object key, Object o) {
        origin_.registerObject(key, o);
    }

    @Override
    public void registerObject(Object key, Object o, Class interfaceClass) {
        origin_.registerObject(key, o, interfaceClass);
    }

    @Override
    public void registerReference(Class clazz) throws Exception {
        origin_.registerReference(clazz);
    }

    @Override
    public void registerSerializer(Serializer serializer) throws Exception {
        origin_.registerSerializer(serializer);
    }

    @Override
    public void registerUnmarshallClassHint(Class externalClass, Class internalClass) {
        origin_.registerUnmarshallClassHint(externalClass, internalClass);
    }

    @Override
    public void registerUnmarshallClassHintTranslator(ClassHintTranslator translator) {
       origin_.registerUnmarshallClassHintTranslator(translator);
    }

    @Override
    public void setCallbackController(CallbackController cbc) {
        origin_.setCallbackController(cbc);
    }

    @Override
    public void setExceptionTransformer(ExceptionTransformer exceptionTransformer) {
        origin_.setExceptionTransformer(exceptionTransformer);
    }

    @Override
    public void unregisterCallback(InvocationCallback callback, Class contextInterface) {
        origin_.unregisterCallback(callback, contextInterface);
    }

    @Override
    public void unregisterClass(String name) {
        origin_.unregisterClass(name);
    }

    @Override
    public void unregisterObject(Object key) {
        origin_.unregisterObject(key);
    }

    //private HttpRequest   request_;
    private JSONRPCBridge origin_;
}
