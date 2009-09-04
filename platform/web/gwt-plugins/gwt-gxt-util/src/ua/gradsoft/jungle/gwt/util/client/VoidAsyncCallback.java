package ua.gradsoft.jungle.gwt.util.client;

import ua.gradsoft.jungle.gwt.util.client.gxt.ErrorMessageBox;
import com.google.gwt.user.client.rpc.AsyncCallback;

/**
 *VoidAsyncCallback, which on succes do nothing (or may be show
 * sucess message in application status if one exists),
 * on error - show supplied message.
 * @author rssh
 */
public class VoidAsyncCallback implements AsyncCallback<Void>
{

    public VoidAsyncCallback(GwtApplication app, String successMessage, String errorMessage)
    {
      app_=app;
      successMessage_=successMessage;
      errorMessage_=errorMessage;
    }

    public void onFailure(Throwable caught) {
        ErrorMessageBox.alert(errorMessage_, caught);
    }

    public void onSuccess(Void result) {
        if (app_!=null && app_.withStatus()) {
            if (successMessage_!=null) {
                app_.getStatus().setText(successMessage_);
            }
        }
    }



    private GwtApplication app_;
    private String         successMessage_;
    private String         errorMessage_;
}
