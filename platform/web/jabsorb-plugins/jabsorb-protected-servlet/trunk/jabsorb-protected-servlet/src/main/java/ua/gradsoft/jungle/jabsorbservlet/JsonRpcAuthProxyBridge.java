
package ua.gradsoft.jungle.jabsorbservlet;

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
 * or redirect one to prixy bridge
 * @author rssh
 */
public class JsonRpcAuthProxyBridge extends JSONRPCBridge
{

    public JsonRpcAuthProxyBridge(JSONRPCBridge proxy)
    {
      proxy_=proxy;  
    }

    @Override
    public void addReference(Object o) {
       proxy_.addReference(o);
    }

    /**
     *TODO: check
     * @param context
     * @param jsonReq
     * @return
     */
    @Override
    public JSONRPCResult call(Object[] context, JSONObject jsonReq) {
          return proxy_.call(context, jsonReq);
    }

    @Override
    public synchronized void enableReferences() throws Exception {
        proxy_.enableReferences();
    }

    @Override
    public CallbackController getCallbackController() {
        return proxy_.getCallbackController();
    }

    @Override
    public Object getReference(int objectId) {
        return proxy_.getReference(objectId);
    }

    @Override
    public boolean isCallableReference(Class clazz) {
        return proxy_.isCallableReference(clazz);
    }

    @Override
    public boolean isReference(Class clazz) {
        return proxy_.isReference(clazz);
    }

    @Override
    public Class lookupClass(String name) {
        return proxy_.lookupClass(name);
    }

    @Override
    public Object lookupObject(Object key) {
        return proxy_.lookupObject(key);
    }

    @Override
    public void registerCallableReference(Class clazz) throws Exception {
        proxy_.registerCallableReference(clazz);
    }

    @Override
    public void registerCallback(InvocationCallback callback, Class contextInterface) {
        proxy_.registerCallback(callback, contextInterface);
    }

    @Override
    public void registerClass(String name, Class clazz) throws Exception {
        proxy_.registerClass(name, clazz);
    }

    @Override
    public void registerMarshallClassHint(Class internalClass, Class externalClass) {
        proxy_.registerMarshallClassHint(internalClass, externalClass);
    }

    @Override
    public void registerMarshallClassHintTranslator(ClassHintTranslator translator) {
        proxy_.registerMarshallClassHintTranslator(translator);
    }

    @Override
    public void registerObject(Object key, Object o) {
        proxy_.registerObject(key, o);
    }

    @Override
    public void registerObject(Object key, Object o, Class interfaceClass) {
        proxy_.registerObject(key, o, interfaceClass);
    }

    @Override
    public void registerReference(Class clazz) throws Exception {
        proxy_.registerReference(clazz);
    }

    @Override
    public void registerSerializer(Serializer serializer) throws Exception {
        proxy_.registerSerializer(serializer);
    }

    @Override
    public void registerUnmarshallClassHint(Class externalClass, Class internalClass) {
        proxy_.registerUnmarshallClassHint(externalClass, internalClass);
    }

    @Override
    public void registerUnmarshallClassHintTranslator(ClassHintTranslator translator) {
       proxy_.registerUnmarshallClassHintTranslator(translator);
    }

    @Override
    public void setCallbackController(CallbackController cbc) {
        proxy_.setCallbackController(cbc);
    }

    @Override
    public void setExceptionTransformer(ExceptionTransformer exceptionTransformer) {
        proxy_.setExceptionTransformer(exceptionTransformer);
    }

    @Override
    public void unregisterCallback(InvocationCallback callback, Class contextInterface) {
        proxy_.unregisterCallback(callback, contextInterface);
    }

    @Override
    public void unregisterClass(String name) {
        proxy_.unregisterClass(name);
    }

    @Override
    public void unregisterObject(Object key) {
        proxy_.unregisterObject(key);
    }

    //private HttpRequest   request_;
    private JSONRPCBridge proxy_;
}
