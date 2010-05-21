
package ua.gradsoft.jungle.gwt.util.client.gxt;

import com.extjs.gxt.ui.client.widget.LayoutContainer;

/**
 *Helper for layout cointainer
 * @author rssh
 */
public class LayoutContainerHelper {

    public static void adjustHeightToParent(LayoutContainer component)
    {
       if (component.getParent() instanceof LayoutContainer) {
              LayoutContainer bp = (LayoutContainer)component.getParent();
              component.setAutoHeight(false);
              component.setHeight(bp.getHeight(true));
       }
    }


}
