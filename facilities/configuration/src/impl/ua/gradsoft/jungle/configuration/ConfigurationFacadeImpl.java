package ua.gradsoft.jungle.configuration;


import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.ejb.Local;
import javax.ejb.Remote;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import ua.gradsoft.jungle.persistence.cluster_keys.ClusterKeys;
import ua.gradsoft.jungle.persistence.jpaex.JdbcConnectionWrapper;
import ua.gradsoft.persistence.ejbqlao.EjbQlAccessObject;

@Stateless
@Remote(ConfigurationFacade.class)
@Local(ConfigurationFacade.class)
public class ConfigurationFacadeImpl extends EjbQlAccessObject implements ConfigurationFacade
{


    public List<ConfigItem> getConfigItems(String appName) {
        HashMap<String, Object> vars = new HashMap<String,Object>();
        vars.put("name", appName);
        Map<String,Object> options = Collections.emptyMap();
        return executeQuery(ConfigItem.class,
                "select c from ConfigItemDescription c where c.appname=:name",
                vars, options
                );
    }

    public BigDecimal registerConfigItem(ConfigItem description) {
        // if is is null - get id.
        BigDecimal id = null;
        if (description.getId()==null) {
            JdbcConnectionWrapper cnw = getJpaEx().getJdbcConnectionWrapper(entityManager_, false);
            Connection cn = cnw.getConnection();
            id=ClusterKeys.generateBigDecimalClusterKeyBySequence("jungle_configitems", cn);
            cnw.releaseConnection(cn);
            description.setId(id);
        }else{
            id=description.getId();
        }
        entityManager_.persist(description);
        return id;
    }

    public void unregisterConfigItem(BigDecimal id) {
        ConfigItem x = entityManager_.find(ConfigItem.class, id);
        if (x!=null) {
            entityManager_.remove(x);
        }
    }



    public <T extends Serializable> T getConfigItemValue(Class<T> type, ConfigItemSelector itemSelector) {
        List<ConfigItem> items=queryByCriteria(ConfigItem.class, itemSelector);
        ConfigItem item = items.get(0);
        return (T)item.getType().fromString(item.getValue());
    }

    public BigDecimal getBigDecimalConfigItemValue(ConfigItemSelector selector) {
        return getConfigItemValue(BigDecimal.class,selector);
    }

    public Boolean getBooleanConfigItemValue(ConfigItemSelector selector) {
        return getConfigItemValue(Boolean.class,selector);
    }

    public Double getDoubleConfigItemValue(ConfigItemSelector selector) {
        return getConfigItemValue(Double.class,selector);
    }

    public Integer getIntegerConfigItemValue(ConfigItemSelector selector) {
        return getConfigItemValue(Integer.class,selector);
    }

    public String getStringConfigItemValue(ConfigItemSelector selector) {
        return getConfigItemValue(String.class,selector);
    }


    public List<Serializable> getConfigItemValues(ConfigItemSelector itemSelector) {
        List<Serializable> retval = new ArrayList<Serializable>();
        List<ConfigItem> items=queryByCriteria(ConfigItem.class, itemSelector);
        for(ConfigItem item: items) {
            retval.add(item.getType().fromString(item.getValue()));
        }
        return retval;
    }


    public <T extends Serializable> void setConfigItemValue(ConfigItemSelector itemSelector, T value) {
        List<ConfigItem> items=queryByCriteria(ConfigItem.class, itemSelector);
        ConfigItem curr = items.get(0);
        curr.setValue(curr.getType().toString(value));
        curr=entityManager_.merge(curr);
    }

    public void setConfigItemValues(Map<BigDecimal, Serializable> objects) {
       for(Map.Entry<BigDecimal,Serializable> e: objects.entrySet()) {
           ConfigItem curr = entityManager_.find(ConfigItem.class, e.getKey());
           curr.setValue(curr.getType().toString(e.getValue()));
           entityManager_.merge(curr);
       }
    }



  public EntityManager getEntityManager()
  { return entityManager_; }
 
  @PersistenceContext 
  private EntityManager entityManager_;
}
