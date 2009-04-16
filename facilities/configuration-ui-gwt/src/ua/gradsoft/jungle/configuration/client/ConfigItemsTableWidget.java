/**
 * Part of GradSoft Jungle framework.
 *
 */
package ua.gradsoft.jungle.configuration.client;

import com.extjs.gxt.ui.client.Events;
import com.extjs.gxt.ui.client.data.BasePagingLoader;
import com.extjs.gxt.ui.client.data.BeanModel;
import com.extjs.gxt.ui.client.data.BeanModelReader;
import com.extjs.gxt.ui.client.data.LoadEvent;
import com.extjs.gxt.ui.client.data.PagingLoadConfig;
import com.extjs.gxt.ui.client.data.RpcProxy;
import com.extjs.gxt.ui.client.event.GridEvent;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.LoadListener;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.store.Record.RecordUpdate;
import com.extjs.gxt.ui.client.store.StoreEvent;
import com.extjs.gxt.ui.client.store.StoreListener;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.MessageBox;
import com.extjs.gxt.ui.client.widget.PagingToolBar;
import com.extjs.gxt.ui.client.widget.StatusBar;
import com.extjs.gxt.ui.client.widget.form.Field;
import com.extjs.gxt.ui.client.widget.form.TextField;
import com.extjs.gxt.ui.client.widget.grid.CellEditor;
import com.extjs.gxt.ui.client.widget.grid.ColumnConfig;
import com.extjs.gxt.ui.client.widget.grid.ColumnModel;
import com.extjs.gxt.ui.client.widget.grid.EditorGrid;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.extjs.gxt.ui.client.widget.layout.FlowLayout;
import com.google.gwt.user.client.rpc.AsyncCallback;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import ua.gradsoft.jungle.configuration.ConfigItem;
import ua.gradsoft.jungle.configuration.ConfigItemSelectorByNames;
import ua.gradsoft.jungle.configuration.ConfigItemType;
import ua.gradsoft.jungle.gwt.util.client.gxt.PagingLoadCallbackAdapters;

/**
 *Table widget for Config Items
 */
public class ConfigItemsTableWidget extends LayoutContainer
{

    public ConfigItemsTableWidget(ConfigurationUI configurationUI, String appName)
    {
       configurationUI_ = configurationUI;
       FlowLayout layout = new FlowLayout(10);
       setLayout(layout);

       final String fAppName = appName;
       
       RpcProxy proxy = new RpcProxy() {
          @Override
          public void load(Object loadConfig, AsyncCallback callback) {
              PagingLoadConfig pagingLoadConfig = (PagingLoadConfig)loadConfig;
              ConfigItemSelectorByNames selector = new ConfigItemSelectorByNames(null,null);
              selector.setAppName(fAppName);
              PagingLoadCallbackAdapters adapters = new PagingLoadCallbackAdapters<ConfigItem>(callback,pagingLoadConfig.getOffset());

              selector.setFirstResult(pagingLoadConfig.getOffset());
              selector.setMaxResults(pagingLoadConfig.getLimit());
              configurationUI_.getService().getConfigItems(selector, 
                                                           adapters.getListCallbackAdapter());
              configurationUI_.getService().getConfigItemsCount(selector, adapters.getTotalCountCallbackAdapter());
          }
       };
       
       BeanModelReader reader = new BeanModelReader();

       ConfigItem item = new ConfigItem();
       item.setId(BigDecimal.valueOf(1L));
       item.setName("name-value");

       BasePagingLoader loader = new BasePagingLoader(proxy, reader);
       ListStore<BeanModel> store = new ListStore<BeanModel>(loader);
       final PagingToolBar toolBar = new PagingToolBar(50);
       toolBar.bind(loader);

       //loader.setOffset(0);
       //loader.setLimit(5);

       loader.setRemoteSort(true);
       loader.load(0, 5);
    
       loader.addLoadListener(new ConfigItemsLoadExceptionListener() );

       store.addStoreListener( new ConfigItemsStoreBackendListener() );

       List<ColumnConfig> columns = new ArrayList<ColumnConfig>();
       ColumnConfig columnConfig = new ColumnConfig("name","name",100);
       columns.add(columnConfig);

       //TODO: change to use AdapterCellEditor when needed
       columnConfig = new ColumnConfig("value","value",100);
       TextField<String> tfsValue = new TextField<String>();
       tfsValue.setAllowBlank(false);
       tfsValue.setAutoValidate(true);
       CellEditor valueCellEditor = new ValueCellEditor(tfsValue);
       columnConfig.setEditor(valueCellEditor);


       columns.add(columnConfig);

       columnConfig = new ColumnConfig("description","description",400);
       columns.add(columnConfig);

       //columnConfig = new ColumnConfig("xxx","xxx",30);
       //columns.add(columnConfig);
       

       ColumnModel cm = new ColumnModel(columns);


       EditorGrid<BeanModel> grid = new EditorGrid<BeanModel>(store,cm);

       grid.addListener(Events.BeforeEdit, new Listener<GridEvent>(){
           public void handleEvent(GridEvent e) {
               Object oeditable = e.model.get("editable");
               boolean editable = (oeditable!=null && (Boolean)oeditable);
               if (!editable) {
                   e.doit=false;
               }else{
                   ConfigItemType type = (ConfigItemType)e.model.get("type");
                   ColumnModel cm = e.grid.getColumnModel();                                
                   int columnIndex = cm.getIndexById("value");           
                   ValueCellEditor vce = (ValueCellEditor)cm.getEditor(columnIndex);
                   vce.setConfigItemType(type);
                   vce.setItemName((String)e.model.get("name"));
                   vce.setPreviousValue(e.value);
               }               
           }
       });

       grid.setAutoHeight(true);
       grid.setAutoWidth(true);

       ContentPanel cn = new ContentPanel();
       cn.setFrame(true);
       cn.setCollapsible(true);
       cn.setIconStyle("icon-table");
       cn.setHeading("Configuration for "+fAppName);
       cn.setLayout(new FitLayout());
       cn.setBodyBorder(true);
       cn.add(grid);
       //cn.setSize(600, 350);
       cn.setBottomComponent(toolBar);

       //cn.setAutoWidth(true);
       //cn.setAutoHeight(true);

       //setAutoHeight(true);
       //setAutoWidth(true);

       add(cn);
    }

    /**
     * @return parent status bar if was set, otherwise - return null
     */
    public StatusBar getStatusBar()
    {
        return statusBar_;
    }
    
    /**
     * Set parent status bar. When one is set - TableWidget shows on it 
     * current state.  
     */
    public void setStatusBar(StatusBar statusBar)
    {
        statusBar_=statusBar;
    }

    static class ValueCellEditor extends CellEditor
    {
        public ValueCellEditor(Field f)
        {
          super(f);  
        }
        
        public void setConfigItemType(ConfigItemType type)
        {
           configItemType_=type; 
        }

        public void setPreviousValue(Object value)
        {
            previousValue_=value;
        }

        public void setItemName(String itemName)
        {
           itemName_=itemName;
        }

        @Override
        public Object postProcessValue(Object value) {
            if (configItemType_==null) {
                MessageBox.alert("warning", "item type is not set", null);
            }           
            if (value==null) {
                return null;
            }
            Object retval=value;
            //getField().clearInvalid();
            switch(configItemType_) {
                case STRING:
                    break;
                case BOOLEAN:                
                    retval = Boolean.toString(Boolean.parseBoolean((String)value));                      
                    break;
                case INTEGER:
                    try {
                        int n = Integer.parseInt((String)value);
                    }catch(NumberFormatException ex){
                        MessageBox mb = MessageBox.alert("Incorrect format", "Item '"+itemName_+"' must be integer, have "+value, null);
                        retval=previousValue_;
                        //getField().forceInvalid("must be integer");
                    }
                    break;
                case BIGDECIMAL:
                    try {
                        BigDecimal bd = new BigDecimal((String)value);
                    }catch(NumberFormatException ex){
                        MessageBox mb = MessageBox.alert("Incorrect format", "item '"+itemName_+"' must be a number", null);
                        retval = previousValue_;
                        //getField().forceInvalid("must be big decimal");
                    }
                    break;
                case DOUBLE:
                    try {
                        Double d = Double.parseDouble((String)value);
                    }catch(NumberFormatException ex){
                        MessageBox mb = MessageBox.alert("Incorrect format", "item '"+itemName_+"' must be a number", null);
                        retval = previousValue_;
                        //getField().forceInvalid("must be double");
                    }
                    break;                    
                default:
                    MessageBox.alert("internal error","Unknown type:"+configItemType_,null);                  
                    break;                
            }
            return retval;
        }
        
        private ConfigItemType configItemType_ = null;
        private Object         previousValue_ = null;
        private String         itemName_ = null;
    }

    class ConfigItemsStoreBackendListener<M extends BeanModel> extends StoreListener<M>
    {
        @Override
        public void storeUpdate(StoreEvent<M> event) {
            if (event.operation!=RecordUpdate.REJECT) {
               BeanModel model = event.model;
               ConfigItem ci = (ConfigItem)model.getBean();
               // singletonMap is not in list of serialized classes.
               //Map<BigDecimal,String> param = Collections.singletonMap(ci.getId(), ci.getValue());
               Map<BigDecimal,String> param = new LinkedHashMap<BigDecimal,String>();
               param.put(ci.getId(), ci.getValue());
               configurationUI_.getService().setConfigItemStringValues(param,
                       new ConfigItemsStoreCallback()
                       );
            }
        }
    }


    static class ConfigItemsLoadExceptionListener extends LoadListener
    {
           @Override
           public void loaderLoadException(LoadEvent event) {
               event.exception.printStackTrace();
               MessageBox mb = MessageBox.alert("exception during data load", event.exception.getMessage() , null);
           }

    }

    class ConfigItemsStoreCallback implements AsyncCallback<Void>
    {

        public void onFailure(Throwable caught) {
            caught.printStackTrace();
            MessageBox mb = MessageBox.alert("fail to save value:", caught.getMessage() , null);
        }

        public void onSuccess(Void result) {
            if (statusBar_!=null) {
                statusBar_.setMessage("value saved");
            }
        }

    }

    private StatusBar       statusBar_;
    private ConfigurationUI configurationUI_;
}
