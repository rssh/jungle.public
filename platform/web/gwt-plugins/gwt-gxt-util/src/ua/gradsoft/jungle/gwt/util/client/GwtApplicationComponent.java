package ua.gradsoft.jungle.gwt.util.client;

/**
 *GwtApplicationComponent - one unit of gwt application.
 * (Service, or UI part).
 * @author rssh
 */
public abstract class GwtApplicationComponent
{

    /**
     * @return name of application component
     */
    public abstract String getName();

    /**
     * Called when component is registered in application.
     * (initialize stuff here)
     */
    public void onRegistered()
    {}

    /**
     * Called when component is unregistered in application.
     * (shutdown stuff here)
     */
    public void onUnregistered()
    {}

    /**
     * Called when user is logged into application.
     * (if application support login/logout)
     */
    public void onLogin()
    {}

    /**
     * Called when user is logged out from application.
     * (if application support login/logout)
     */
    public void onLogout()
    {}

    /**
     * Called on custom event.
     */
    public void onCustomEvent()
    {}

    /**
     * get application. 
     * @return
     */
    public GwtApplication getApplication()
    { return application_; }
    
    void setGwtApplication(GwtApplication application)
    { application_=application; }


    /**
     * GwtApplication - reference to main application.
     *Setted by engine before first call to onRegistered. 
     */
    protected GwtApplication application_;
}
