package ua.gradsoft.jungle.gwt.util.client.gxt;

import com.extjs.gxt.ui.client.widget.Component;
import ua.gradsoft.jungle.gwt.util.client.ValidationException;

/**
 *EventListener for detaisl in tab,
 * @author rssh
 */
public interface DetailsTabsEventListener {

    public void handleTabEvent(int event, Component source) throws ValidationException;


}

