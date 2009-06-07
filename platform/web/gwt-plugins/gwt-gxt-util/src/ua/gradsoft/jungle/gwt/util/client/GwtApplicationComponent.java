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
    public void onRegistered(GwtApplication application)
    {}

    /**
     * Called when component is unregistered in application.
     * (shutdown stuff here)
     */
    public void onUnregistered(GwtApplication application)
    {}

    /**
     * Called when user is logged into application.
     * (if application support login/logout)
     */
    public void onLogin(GwtApplication application)
    {}

    /**
     * Called when user is logged out from application.
     * (if application support login/logout)
     */
    public void onLogout(GwtApplication application)
    {}

    /**
     * Called on custom event.
     */
    public void onCustomEvent(GwtApplicationEvent event)
    {}


}
