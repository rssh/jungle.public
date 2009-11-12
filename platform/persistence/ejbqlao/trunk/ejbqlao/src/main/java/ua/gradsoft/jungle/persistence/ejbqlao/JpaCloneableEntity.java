
package ua.gradsoft.jungle.persistence.ejbqlao;

/**
 *Entity with user-supplied clone. Sence of user-supplied clone is in
 * creation of POJO, which is detached from database (and therefore
 * can be changed does not bound with changes in database).
 * @author rssh
 */
public interface JpaCloneableEntity<T extends JpaEntity> extends JpaEntity
{

    public T  cloneEntity();

}
