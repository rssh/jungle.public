
package ua.gradsoft.jungle.gwt.util.client.gxt;

import com.extjs.gxt.ui.client.Style.HorizontalAlignment;
import com.extjs.gxt.ui.client.data.BeanModel;
import com.extjs.gxt.ui.client.event.ComponentEvent;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.widget.Component;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.form.FormPanel;
import com.extjs.gxt.ui.client.widget.selection.StoreSelectionModel;
import java.util.List;
import ua.gradsoft.jungle.gwt.util.client.ValidationException;

/**
 *Base detail widget, which can situated inside detail tabs
 * @author rssh
 */
public abstract class BaseDetailFormPanel extends FormPanel
                                  implements BeanModelDetailWidget,
                                             DetailsTabsEventListener
{

    public BaseDetailFormPanel(SelectionListWithDetailBaseWidget owner)
    { owner_=owner; }
    
    protected abstract void fillDataAndFormWithBeanModel(BeanModel bm) throws ValidationException;

    protected  void clearDataAndForm(BeanModel bm) {}

    protected abstract void fillFormFromData();

    protected abstract boolean fillDataFromForm() throws ValidationException;

    protected abstract void saveData();
    
    protected void rejectData()
    {
      fillFormFromData();
    }

    public void handleTabEvent(int event, Component source) throws ValidationException
    {
                if (event==DetailsTabsEvents.TABS_CLOSED || event==DetailsTabsEvents.TAB_CLOSED) {
                    saveData();
                } else if (event==DetailsTabsEvents.TABS_OPENED || event==DetailsTabsEvents.TAB_OPENED) {
                    BeanModelSelectionWidget selectionWidget = owner_.getSelection();
                    StoreSelectionModel<BeanModel> sl = selectionWidget.getSelectionModel();
                    List<BeanModel> selected = sl.getSelectedItems();
                    BeanModel bm = null;
                    if (selected.size()>0) {
                        bm = selected.get(0);
                    }else{
                        return;
                    }
                    owner_.fireBeanModelSelectionEvent(BeanModelSelectionEvents.SELECT, bm);
                }

    }

    public void handleBeanModelSelectionEvent(int event, BeanModel bm) throws ValidationException
    {
        if (event==BeanModelSelectionEvents.ADD || event==BeanModelSelectionEvents.SELECT) {
            fillDataAndFormWithBeanModel(bm);
        } else if (event==BeanModelSelectionEvents.DELETE) {
            clearDataAndForm(bm);
        }
    }

    protected void addCancelSubmitButtons()
    {
      setButtonAlign(HorizontalAlignment.CENTER);
      Button submitButton = new Button("Submit");
      addButton(submitButton);
      Button cancelButton = new Button("Cancel");
      addButton(cancelButton);

      submitButton.addSelectionListener(new SelectionListener(){
            @Override
            public void componentSelected(ComponentEvent ce) {
                saveData();
            }
      }
      );

      cancelButton.addSelectionListener(new SelectionListener(){
            @Override
            public void componentSelected(ComponentEvent ce) {
                rejectData();
            }
      }
      );

    }

    protected SelectionListWithDetailBaseWidget owner_;

}
