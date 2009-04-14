
package ua.gradsoft.jungle.gwt.util.client.gxt;

import com.extjs.gxt.ui.client.data.BeanModel;
import com.extjs.gxt.ui.client.data.BeanModelFactory;
import com.extjs.gxt.ui.client.data.BeanModelLookup;
import com.google.gwt.user.client.rpc.AsyncCallback;
import java.util.ArrayList;
import java.util.List;

/**
 *Bean callback adapter for list. (Used for gxt tree store)
 * @author rssh
 */
public class BeanModelListCallbackAdapter<T> implements AsyncCallback<List<T>>
{
    
    public BeanModelListCallbackAdapter(AsyncCallback<List<BeanModel>> origin)
    {
      origin_=origin;
    }

    public void onFailure(Throwable caught) {
       origin_.onFailure(caught);
    }

    public void onSuccess(List<T> result) {
        List<BeanModel> l=null;
        if (result.size()!=0) {
            l=new ArrayList<BeanModel>();
            for(T t: result) {
               BeanModelFactory factory = BeanModelLookup.get().getFactory(t.getClass());
               if (factory==null) {
                   throw new RuntimeException("No BeanModelFactory found for "+ t.getClass());
               }
               l.add(factory.createModel(t));
            }
        }else{
            // Collections.emptyList is not in gwt runtime.
            l=new ArrayList<BeanModel>();
        }
        origin_.onSuccess(l);

    }

    private AsyncCallback<List<BeanModel>> origin_;
}
