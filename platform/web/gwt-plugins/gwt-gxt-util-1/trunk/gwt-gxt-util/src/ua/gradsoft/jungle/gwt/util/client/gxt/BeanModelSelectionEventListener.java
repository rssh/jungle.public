package ua.gradsoft.jungle.gwt.util.client.gxt;

import com.extjs.gxt.ui.client.data.BeanModel;
import ua.gradsoft.jungle.gwt.util.client.ValidationException;

/**
 *Listener for BeanModel events
 * @author rssh
 */
public interface BeanModelSelectionEventListener {

   public void handleBeanModelSelectionEvent(int event, BeanModel bm) throws ValidationException;

}
