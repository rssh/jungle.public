
package ua.gradsoft.jungle.gwt.util.client.gxt;

import com.extjs.gxt.ui.client.widget.MessageBox;

/**
 *MessageBox which shown on error.
 * @author rssh
 */
public class ErrorMessageBox {

    public static void alert(String message, Throwable ex)
    {
     // ex.printStackTrace();
     MessageBox.alert("error", message+":"+ex.getMessage(), null);   
    }

}
