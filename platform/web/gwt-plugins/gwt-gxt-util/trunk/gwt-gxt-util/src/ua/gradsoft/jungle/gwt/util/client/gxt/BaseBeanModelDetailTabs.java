
package ua.gradsoft.jungle.gwt.util.client.gxt;

import com.extjs.gxt.ui.client.data.BeanModel;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.TabPanelEvent;
import com.extjs.gxt.ui.client.widget.BoxComponent;
import com.extjs.gxt.ui.client.widget.Component;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.MessageBox;
import com.extjs.gxt.ui.client.widget.TabItem;
import com.extjs.gxt.ui.client.widget.TabPanel;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.google.gwt.core.client.GWT;
import ua.gradsoft.jungle.gwt.util.client.ValidationException;

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
      TabPanel folder = new TabPanel() {

            @Override
            protected void onResize(int width, int height) {
                //GWT.log("BaseBeanModelDetailTabs.resize");
                super.onResize(width, height);
                //int dx = this.getTabWidth()
                for(Component item: getItems()) {
                    //GWT.log("item inside:"+item.getClass().getName());
                    if (item instanceof BoxComponent && item.isRendered()) {
                        //GWT.log("is box component anmd rendered");
                        BoxComponent bcn = (BoxComponent)item;
                        bcn.setSize(this.getWidth(true), this.getHeight(true));
                    }
                }
            }




      };

    
      folder_=folder;
      folder_.setResizeTabs(true);

    
      folder_.addListener(Events.BeforeSelect, new Listener<TabPanelEvent>()
      {
            public void handleEvent(TabPanelEvent te) {            
               TabItem prevSelected = folder_.getSelectedItem();
               TabItem nextSelected = te.getItem();
               boolean cancelled = false;
               if (prevSelected!=nextSelected) {
                   if (prevSelected!=null) {
                       DetailsTabsEventListener l = prevSelected.getData("DetailsTabsEventListener");
                       if (l!=null) {
                         try {
                           l.handleTabEvent(DetailsTabsEvents.TAB_CLOSED,folder_);
                         }catch(ValidationException ex){
                             MessageBox.alert("error", ex.getMessage(), null);
                             te.setCancelled(true);
                             cancelled=true;
                         }
                       }
                   }
               }
               if (!cancelled) {
                  folder_.setData("prevSelected", prevSelected);
               }
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
                         try {
                           l.handleTabEvent(DetailsTabsEvents.TAB_OPENED, folder_);
                         }catch(ValidationException ex){
                             // it means that data incorrect in database.
                             MessageBox.alert("error", ex.getMessage(), null);
                         }
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
      TabItem item = new TabItem() {

            @Override
            protected void onResize(int width, int height) {
                //GWT.log("TabItem.onResize");
                super.onResize(width, height);
                for(Component cn: getItems()) {
                    if (cn.isRendered() && cn instanceof BoxComponent) {
                        BoxComponent bcn = (BoxComponent)cn;
                        //GWT.log("is rendered and box compinent, class="+bcn.getClass().getName());
                        bcn.setSize(getWidth(true), getHeight(true));
                    }
                }
            }


      };


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
    public void handleBeanModelSelectionEvent(int event, BeanModel bm) throws ValidationException
    {
      BeanModelSelectionEventListener l = folder_.getSelectedItem().getData(
                                                     "BeanModelSelectionEventListener");
      if (l!=null) {
            l.handleBeanModelSelectionEvent(event, bm);
      }
    }

    @Override
    protected void onResize(int width, int height) {
        super.onResize(width, height);
        int dx = this.getWidth(false) - this.getWidth(true);
        int dy = this.getHeight(false) - this.getHeight(true);
        folder_.setSize(width-dx, height-dy);
    }
    

    protected SelectionListWithDetailBaseWidget owner_;
    protected TabPanel folder_;
}
