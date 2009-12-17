package ua.gradsoft.hibernateplugin.ritree;

import java.sql.ResultSet;
import org.hibernate.engine.Mapping;
import org.hibernate.engine.SessionImplementor;
import org.hibernate.type.CustomType;

/**
 *
 * @author rssh
 */
public class RiTreeIntervalEntityType extends CustomType
{

    public RiTreeIntervalEntityType(Class userClass)
    {
      super(userClass,null);  
    }
    
    @Override
    public int[] sqlTypes(Mapping pi)
    {
      return SQL_TYPES;  
    }

    public int  getColumnSpan()
    { return 2; }



     static final int[] SQL_TYPES = { java.sql.Types.BIGINT, java.sql.Types.BIGINT  };

}
