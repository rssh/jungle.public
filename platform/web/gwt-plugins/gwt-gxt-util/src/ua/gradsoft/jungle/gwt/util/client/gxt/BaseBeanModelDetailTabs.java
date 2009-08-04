
package ua.gradsoft.jungle.gwt.util.client.gxt;

import com.extjs.gxt.ui.client.data.BeanModel;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.TabPanelEvent;
import com.extjs.gxt.ui.client.widget.Component;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.TabItem;
import com.extjs.gxt.ui.client.widget.TabPanel;

/**
 *Base class for tabs, with details
 * @author rssh
 */
public abstract class BaseBeanModelDetailTabs extends LayoutContainer
                                           implements BeanModelDetailWidget
{

    public BaseBeanModelDetailTabs(SelectionListWithDetailBaseWidget owner)
    {
      owner_=owner;  
      TabPanel folder = new TabPanel();
      folder.setAutoHeight(true);
      folder_=folder;       
      
    
      folder_.addListener(Events.BeforeSelect, new Listener<TabPanelEvent>()
      {
            public void handleEvent(TabPanelEvent te) {            
               TabItem prevSelected = folder_.getSelectedItem();
               TabItem nextSelected = te.getItem();
               if (prevSelected!=nextSelected) {
                   if (prevSelected!=null) {
                       DetailsTabsEventListener l = prevSelected.getData("DetailsTabsEventListener");
                       if (l!=null) {
                         System.err.println("send TAB_CLOSED");
                         l.handleTabEvent(DetailsTabsEvents.TAB_CLOSED,folder_);
                       }
                   }
               }
               folder_.setData("prevSelected", prevSelected);
            }
      });

      folder_.addListener(Events.Select, new Listener<TabPanelEvent>(){
            public void handleEvent(TabPanelEvent te) {              
               TabItem nextSelected = folder_.getSelectedItem();
               TabItem prevSelected = folder_.getData("prevSelected");
               if (prevSelected!=nextSelected) {
                   if (nextSelected!=null) {
                       DetailsTabsEventListener l = nextSelected.getData("DetailsTabsEventListener");
                       if (l!=null) {                       
                           l.handleTabEvent(DetailsTabsEvents.TAB_OPENED, folder_);
                       }else{
                           //System.err.println("nextSelected is not listener");
                       }
                   }else{
                       //System.err.println("nextSelected==null");
                   }
               }else{
                   //System.err.println("nextSelected==prevSelected");
               }
            }
      });

      add(folder_);
      owner.getBeanModelSelectionEventListeners().add(this);            
    }

    protected void addItem(String name,Component cn)
    {
      TabItem item = new TabItem();
      item.setText(name);
      item.add(cn);
      if (cn instanceof BeanModelSelectionEventListener) {
          item.setData("BeanModelSelectionEventListener", cn);
      }
      if (cn instanceof DetailsTabsEventListener) {
          item.setData("DetailsTabsEventListener", cn);
      }
      folder_.add(item);
    }

    @Override
    public void handleBeanModelSelectionEvent(int event, BeanModel bm)
    {
      BeanModelSelectionEventListener l = folder_.getSelectedItem().getData(
                                                     "BeanModelSelectionEventListener");
      if (l!=null) {
            l.handleBeanModelSelectionEvent(event, bm);
      }
    }

    protected SelectionListWithDetailBaseWidget owner_;
    protected TabPanel folder_;
}
