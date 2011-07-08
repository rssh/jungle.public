
package ua.gradsoft.jungle.gwt.util.client.gxt;

import com.extjs.gxt.ui.client.data.BeanModel;
import com.extjs.gxt.ui.client.store.Store;
import com.extjs.gxt.ui.client.widget.selection.StoreSelectionModel;

/**
 *Interface, which can be used by componend
 * @author rssh
 */
public interface BeanModelSelectionWidget {

    public StoreSelectionModel<BeanModel>  getSelectionModel();

    public Store<BeanModel>  getStore();

}
