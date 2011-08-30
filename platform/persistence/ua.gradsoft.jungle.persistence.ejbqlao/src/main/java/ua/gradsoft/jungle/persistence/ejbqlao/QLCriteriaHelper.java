package ua.gradsoft.jungle.persistence.ejbqlao;

import javax.persistence.EntityManager;
import ua.gradsoft.jungle.persistence.ejbqlao.util.QLSelectTemplate;

/**
 *QLCriteriaHelper -- interface for definitions of QL Queries.
 * @author rssh
 */
public abstract class QLCriteriaHelper<T> {


    public abstract void fillSelectTemplate(T criteria,
                                            QLSelectTemplate template,
                                            SelectKind sk,
                                            boolean aggregated, 
                                            String uniquePart);

    public void fillSelectTemplate(T criteria,
                                   QLSelectTemplate template,
                                   SelectKind sk,
                                   boolean aggregated)
    {  fillSelectTemplate(criteria,template,sk,aggregated,""); }

    public EntityManager getEntityManager() {
        return entityManager;
    }

    public void setEntityManager(EntityManager entityManager) {
        this.entityManager = entityManager;
    }




    protected EntityManager entityManager;

}
