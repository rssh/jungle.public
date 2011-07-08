
package ua.gradsoft.jungle.gwt.util.client.gxt;

import com.extjs.gxt.ui.client.Style.Scroll;
import com.extjs.gxt.ui.client.data.BeanModel;
import com.extjs.gxt.ui.client.widget.BoxComponent;
import com.extjs.gxt.ui.client.widget.Component;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.layout.FlowLayout;
import com.google.gwt.core.client.GWT;
import ua.gradsoft.jungle.gwt.util.client.ValidationException;


/**
 *Content Panel with scroll, which shows one elements
 * situated inside, which can be scrolled.
 * @author rssh
 */
public class ScrolledFitContentPanel extends ContentPanel
                                        implements BeanModelDetailWidget,
                                             DetailsTabsEventListener
{


    public ScrolledFitContentPanel(BoxComponent internal)
    {
      this(internal,-1,false);
    }


    public ScrolledFitContentPanel(BoxComponent internal, int height, boolean doLayoutOnEvents)
    {
        setLayout(new FlowLayout());
        setBorders(false);
        setFrame(true);
        setHeaderVisible(false);
        setScrollMode(Scroll.ALWAYS);
        internal_=internal;
        //RowData rld = new RowData();
        //rld.setWidth(1);
        //rld.setHeight(-1);
        if (height > 0) {
          setHeight(height);
        }
        add(internal);
        doLayoutOnEvents_=doLayoutOnEvents;
    }


    public void handleBeanModelSelectionEvent(int event, BeanModel bm) throws ValidationException {
       // GWT.log("ScrolledFitContentPanel.habldeBeanModelSelectionEvent");
       // GWT.log("internal.class is "+internal_.getClass());
        if (internal_ instanceof BeanModelSelectionEventListener) {
           // GWT.log("passed to internal");
            ((BeanModelSelectionEventListener)internal_).handleBeanModelSelectionEvent(event, bm);
            doLayoutOnEvent();
        }
    }

    public void handleTabEvent(int event, Component source) throws ValidationException {
        if (internal_ instanceof DetailsTabsEventListener) {
            ((DetailsTabsEventListener)internal_).handleTabEvent(event, source);
            doLayoutOnEvent();
        }
    }

    protected void doLayoutOnEvent()
    {
      if (doLayoutOnEvents_) {
       if (internal_ instanceof LayoutContainer) {
           ((LayoutContainer)internal_).layout();
       }
      }
    }

    @Override
    protected void onResize(int width, int height) {
        //GWT.log("ScrolledFitContentPanel.onResize");
        super.onResize(width, height);
    }



    protected BoxComponent internal_;
    protected boolean   doLayoutOnEvents_;


}
