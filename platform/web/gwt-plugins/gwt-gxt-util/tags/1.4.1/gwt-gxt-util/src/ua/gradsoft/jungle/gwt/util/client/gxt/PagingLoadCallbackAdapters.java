package ua.gradsoft.jungle.gwt.util.client.gxt;

import com.extjs.gxt.ui.client.data.BasePagingLoadResult;
import com.extjs.gxt.ui.client.data.PagingLoadResult;
import com.google.gwt.user.client.rpc.AsyncCallback;
import java.util.List;

/**
 *Callback adapters from PagingLoadResult to pair of adapters for getting
 *List of results and count of all records.
 * @author rssh
 */
public class PagingLoadCallbackAdapters<T>  {


    public PagingLoadCallbackAdapters(AsyncCallback<PagingLoadResult<T>> baseCallback,
                                      int offset)
    {
      baseCallback_=baseCallback;
      offset_=offset;
    }
    
    public AsyncCallback<List<T>>   getListCallbackAdapter()
    {
        return new PagingListCallbackAdapter();
    }
    
    public AsyncCallback<Integer>   getTotalCountCallbackAdapter()
    {
        return new PagingListCountCallbackAdapter();
    }
    
    
    public synchronized void onJoinSuccess()
    {        
        listReceived_=false;
        countReceived_=false;
        baseCallback_.onSuccess(new BasePagingLoadResult<T>(list_,offset_,totalCount_));        
        list_=null;
    }
    
    public class PagingListCallbackAdapter implements AsyncCallback<List<T>>
    {

        public void onFailure(Throwable caught) {
            System.err.println("failure on list query");
            caught.printStackTrace();
            baseCallback_.onFailure(caught);
        }

        public void onSuccess(List<T> result) {
            list_=result;            
            listReceived_=true;
            if (countReceived_) {
                onJoinSuccess();
            }
        }
        
    }

    public class PagingListCountCallbackAdapter implements AsyncCallback<Integer>
    {

        public void onFailure(Throwable caught) {
            System.err.println("failure on count query");
            caught.printStackTrace();
            baseCallback_.onFailure(caught);
        }

        public void onSuccess(Integer result) {
            totalCount_=result.intValue();
            countReceived_=true;
            if (listReceived_) {
                onJoinSuccess();
            }
        }

    }


    private volatile boolean     listReceived_;
    private List<T>              list_=null;
    private volatile boolean     countReceived_;
    private int                  totalCount_;
    AsyncCallback<PagingLoadResult<T>> baseCallback_;
    private int                     offset_;


}
