package ua.gradsoft.jungle.configuration;


import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import javax.ejb.Local;
import javax.ejb.Remote;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import ua.gradsoft.jungle.persistence.ejbqlao.EjbQlAccessObject;

@Stateless
@Remote(ConfigurationFacade.class)
@Local(ConfigurationFacade.class)
public class ConfigurationFacadeImpl extends EjbQlAccessObject implements ConfigurationFacade
{


    @Override
    @TransactionAttribute(TransactionAttributeType.SUPPORTS)
    public List<ConfigItem> getConfigItems(ConfigItemSelector itemSelector) {
        return queryByCriteria(ConfigItem.class, itemSelector);
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.SUPPORTS)
    public Integer getConfigItemsCount(ConfigItemSelector itemSelector) {
        return queryCountByCriteria(Integer.class, itemSelector);
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    @Override
    public BigDecimal registerConfigItem(ConfigItem description) {
        // if is is null - get id.
        BigDecimal id = null;
        if (description.getId()==null) {
            id = generateNextSequenceKey(ConfigItem.class, BigDecimal.class);
            description.setId(id);
        }else{
            id=description.getId();
        }
        entityManager_.persist(description);
        return id;
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    @Override
    public void unregisterConfigItem(BigDecimal id) {
        ConfigItem x = entityManager_.find(ConfigItem.class, id);
        if (x!=null) {
            entityManager_.remove(x);
        }
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.SUPPORTS)
    public <T extends Serializable> T getConfigItemValue(Class<T> type, ConfigItemSelector itemSelector) {
        List<ConfigItem> items=queryByCriteria(ConfigItem.class, itemSelector);
        ConfigItem item = items.get(0);
        return (T)item.getType().fromString(item.getValue());
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.SUPPORTS)
    public BigDecimal getBigDecimalConfigItemValue(ConfigItemSelector selector) {
        return getConfigItemValue(BigDecimal.class,selector);
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.SUPPORTS)
    public Boolean getBooleanConfigItemValue(ConfigItemSelector selector) {
        return getConfigItemValue(Boolean.class,selector);
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.SUPPORTS)
    public Double getDoubleConfigItemValue(ConfigItemSelector selector) {
        return getConfigItemValue(Double.class,selector);
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.SUPPORTS)
    public Integer getIntegerConfigItemValue(ConfigItemSelector selector) {
        return getConfigItemValue(Integer.class,selector);
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.SUPPORTS)
    public String getStringConfigItemValue(ConfigItemSelector selector) {
        return getConfigItemValue(String.class,selector);
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.SUPPORTS)
    public List<Serializable> getConfigItemValues(ConfigItemSelector itemSelector) {
        List<Serializable> retval = new ArrayList<Serializable>();
        List<ConfigItem> items=queryByCriteria(ConfigItem.class, itemSelector);
        for(ConfigItem item: items) {
            retval.add(item.getType().fromString(item.getValue()));
        }
        return retval;
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public <T extends Serializable> void setConfigItemValue(ConfigItemSelector itemSelector, T value) {
        List<ConfigItem> items=queryByCriteria(ConfigItem.class, itemSelector);
        ConfigItem curr = items.get(0);
        curr.setValue(curr.getType().toString(value));
        entityManager_.merge(curr);
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public void setConfigItemValues(Map<BigDecimal, Serializable> objects) {
       for(Map.Entry<BigDecimal,Serializable> e: objects.entrySet()) {
           ConfigItem curr = entityManager_.find(ConfigItem.class, e.getKey());
           curr.setValue(curr.getType().toString(e.getValue()));
           entityManager_.merge(curr);
       }
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public void  setConfigItemStringValues(Map<BigDecimal,String> objects) {
        for(Map.Entry<BigDecimal,String> e: objects.entrySet()) {
            ConfigItem curr = entityManager_.find(ConfigItem.class, e.getKey());
            curr.setValue(e.getValue());
            entityManager_.merge(curr);
        }
    }

  @Override
  public EntityManager getEntityManager()
  { return entityManager_; }
 
  @PersistenceContext
  private EntityManager entityManager_;
}
