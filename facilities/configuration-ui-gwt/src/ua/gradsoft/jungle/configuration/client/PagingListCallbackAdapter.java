package ua.gradsoft.jungle.configuration.client;

import com.extjs.gxt.ui.client.data.BasePagingLoadResult;
import com.extjs.gxt.ui.client.data.PagingLoadResult;
import com.google.gwt.user.client.rpc.AsyncCallback;
import java.util.List;

/**
 *Adapter
 * @author rssh
 */
public class PagingListCallbackAdapter<T> implements AsyncCallback<List<T>>
{


    public PagingListCallbackAdapter(AsyncCallback<PagingLoadResult<T>> baseCallback,
                                     int offset)
    {
      baseCallback_=baseCallback;
      offset_=offset;   
    }


    public void onFailure(Throwable caught) {
        baseCallback_.onFailure(caught);
    }

    public void onSuccess(List<T> result) {
       baseCallback_.onSuccess(new BasePagingLoadResult(result,offset_,offset_+result.size()));
    }

    private AsyncCallback<PagingLoadResult<T>> baseCallback_;
    private int                    offset_;

}
