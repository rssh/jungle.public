
package ua.gradsoft.jungle.gwt.util.client.gxt;

import com.extjs.gxt.ui.client.data.ListLoader;
import com.extjs.gxt.ui.client.data.LoadEvent;
import com.extjs.gxt.ui.client.data.PagingLoader;
import com.extjs.gxt.ui.client.event.BaseEvent;
import com.extjs.gxt.ui.client.event.ComponentEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.KeyListener;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.LoadListener;
import com.extjs.gxt.ui.client.widget.form.TextField;
import com.google.gwt.core.client.GWT;

/**
 *
 * @author rssh
 */
public abstract class FilterToolField<T,S> extends TextField<String> {

    public FilterToolField(ListLoader loader,
                           DataProxyWithSelector<T,S> proxy)
    {
      loader_=loader;
      proxy_=proxy;
      //setFireChangeEventOnSetValue(true);
      //addListener(Events.Change, new Listener<BaseEvent>(){
      //
      //      public void handleEvent(BaseEvent be) {
      //          onChange();
      //      }
      //
      //});
      addKeyListener(new KeyListener(){

            @Override
            public void componentKeyPress(ComponentEvent event) {
                onChange();
            }

      });
      loader_.addLoadListener(new LoadListener(){

            @Override
            public void loaderLoad(LoadEvent le) {
                onListLoad(le);
            }

      });

      Listener<BaseEvent> changeListener = new Listener<BaseEvent>(){

            public void handleEvent(BaseEvent be) {
                GWT.log("handleEvent:"+be.toString());
                onChange();
            }

      };

      this.addListener(Events.Clear, changeListener);

      this.addListener(Events.OnPaste, changeListener);

      //this.addListener(Events.SpecialKey, changeListener);


    }

    public boolean getReuseSelector()
    {
      return reuseSelector_;
    }

    public void setReuseSelector(boolean reuseSelector)
    {
      reuseSelector_  = reuseSelector;
    }


    protected void onChange()
    {
      if (waitForLoad_) {
          existsNextRequest_=true;
      }else{
          waitForLoad_=true;
          S selector=null;
          if (reuseSelector_) {
            selector = proxy_.getSelector();
            if (proxy_.getSelector()==null) {
              proxy_.setSelector(createSelector());
              selector=proxy_.getSelector();
            }
            fillSelector(selector);
          }else{
            selector = createSelector();
            fillSelector(selector);
            proxy_.setSelector(selector);
          }
          //GWT.log("call load, value="+getValue());
          if (loader_ instanceof PagingLoader) {
            PagingLoader pg = (PagingLoader)loader_;
            int prevLimit=pg.getLimit();
            pg.load(0, prevLimit);
          } else {
            loader_.load();
          }
      }
    }

    /**
     * fill selector by data, which now typed in fill value.
     * @param selector
     */
    protected abstract void fillSelector(S selector);

    /**
     * create selector.
     * if <code> reuseSelector </code> is set to true,
     *  then this method must create selector, which
     *  later will be filled by <code> fillSelector </code>,
     *  before each update. Otherwise new selector will
     *  be called on each update.
     * @return
     */
    protected abstract S    createSelector();


    protected void onListLoad(LoadEvent loadEvent)
    {
        waitForLoad_=false;
        if (existsNextRequest_) {
            existsNextRequest_=false;
            onChange();
        }
    }

    private boolean waitForLoad_;
    private boolean existsNextRequest_;
    private boolean reuseSelector_;
    private ListLoader loader_;
    private DataProxyWithSelector<T,S>  proxy_;


}
