package ua.gradsoft.jungle.persistence.jpaex;

/**
 *thown, when we search for property in JPA Entity.
 * @author rssh
 */
public class JpaEntityPropertyNotFoundException extends RuntimeException
{

    public JpaEntityPropertyNotFoundException(Class entityClass, String name)
    {
        super("property "+name+" is not found for class "+entityClass.getName());
    }

    public JpaEntityPropertyNotFoundException(Class entityClass)
    {
        super("property is not found for class "+entityClass.getName());
    }

    public JpaEntityPropertyNotFoundException()
    {
        super("JPA entity property is not found");
    }


}
