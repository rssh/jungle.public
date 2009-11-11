
package ua.gradsoft.jungle.gwt.util.client.gxt;

import com.extjs.gxt.ui.client.widget.MessageBox;

/**
 *MessageBox which shown on error.
 * @author rssh
 */
public class ErrorMessageBox {

    public static void alert(String message, Throwable ex)
    {
     if (ex!=null) {
       MessageBox.alert("error", message+":"+ex.getMessage(), null);
     }else{
       alert(message);
     }
    }

    public static void alert(String message)
    {
     // ex.printStackTrace();
     MessageBox.alert("error", message, null);
    }


}
