
package ua.gradsoft.jungle.gwt.util.client.gxt;

import com.extjs.gxt.ui.client.data.LoadEvent;
import com.extjs.gxt.ui.client.event.LoadListener;
import com.extjs.gxt.ui.client.widget.MessageBox;

/**
 *Listener for exception
 * @author rssh
 */
public class ExceptionLoadListener extends LoadListener
{

          @Override
           public void loaderLoadException(LoadEvent event) {
               event.exception.printStackTrace();
               MessageBox mb = MessageBox.alert("exception during data load",event.exception.toString()+":"+event.exception.getMessage() , null);
           }


}
