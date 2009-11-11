package ua.gradsoft.jungle.gwt.util.client;

import java.util.Map;

/**
 *ApplicationEvent
 */
public class GwtApplicationEvent
{

    public GwtApplicationEvent(String eventName, String sourceName, GwtApplication application, Map<String,Object> params)
    {
      eventName_=eventName;
      sourceName_=sourceName;
      application_=application;
      params_=params;
    }

    public String getEventName()
    { return eventName_; }

    public String getSourceComponentName()
    { return sourceName_; }

    public GwtApplication getApplication()
    { return application_; }
    
    public Map<String,Object> getParams()
    { return params_; }


    private String         eventName_;
    private String         sourceName_;
    private GwtApplication application_;
    private Map<String,Object> params_;

}
