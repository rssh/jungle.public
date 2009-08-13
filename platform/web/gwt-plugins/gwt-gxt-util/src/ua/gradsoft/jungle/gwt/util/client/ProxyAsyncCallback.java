package ua.gradsoft.jungle.gwt.util.client;

import com.google.gwt.user.client.rpc.AsyncCallback;

/**
 * Proxy Callback, where we can overload onSuccess ot onFailure methods
 *if needed.
 */
public class ProxyAsyncCallback<V,T> implements AsyncCallback<T>
{

    public ProxyAsyncCallback(V value, AsyncCallback<T> origin)
    {
      value_=value;
      origin_=origin;
    }

    public void onFailure(Throwable caught) {
        origin_.onFailure(caught);
    }

    public void onSuccess(T result) {
        origin_.onSuccess(result);
    }



    protected V                value_;
    protected AsyncCallback<T> origin_;

}
