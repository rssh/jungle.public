
package ua.gradsoft.jungle.gwt.util.client.gxt;

import com.extjs.gxt.ui.client.data.DataProxy;

/**
 *DataProxy, whew we can manipulate selector.
 * @author rssh
 */
public interface DataProxyWithSelector<T,S> extends DataProxy<T> {

    public S getSelector();
    public void  setSelector(S selector);


}
