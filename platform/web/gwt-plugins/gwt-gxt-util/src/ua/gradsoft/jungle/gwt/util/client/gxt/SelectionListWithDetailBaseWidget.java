
package ua.gradsoft.jungle.gwt.util.client.gxt;

import com.extjs.gxt.ui.client.data.BeanModel;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import java.util.LinkedList;
import java.util.List;

/**
 *Base widget for selection list with details
 * @author rssh
 */
public class SelectionListWithDetailBaseWidget extends LayoutContainer
{

    public SelectionListWithDetailBaseWidget()
    {
      beanModelSelectionEventListeners_ = new LinkedList<BeanModelSelectionEventListener>();
    }


    public List<BeanModelSelectionEventListener> getBeanModelSelectionEventListeners()
    {
      return beanModelSelectionEventListeners_;
    }

    public void fireBeanModelSelectionEvent(int event, BeanModel bm)
    {
       for(BeanModelSelectionEventListener l: beanModelSelectionEventListeners_) {
           l.handleBeanModelSelectionEvent(event, bm);
       }
    }

    public BeanModelSelectionWidget getSelection()
    { return selection_; }

    public BeanModelDetailWidget    getDetail()
    { return detail_; }


    protected BeanModelSelectionWidget selection_;
    protected BeanModelDetailWidget    detail_;
    private List<BeanModelSelectionEventListener> beanModelSelectionEventListeners_;

}
