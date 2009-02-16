package ua.gradsoft.caching.util;

import java.io.Serializable;
import ua.gradsoft.caching.ValueTransformer;

/**
 *Value transformer with id operation
 * @author rssh
 */
public class IdValueTransformer implements ValueTransformer<Object, Serializable>
{

    public Object fromCache(Serializable ct) {
        return ct;
    }

    public Serializable toCache(Object ot) {
        return (Serializable)ot;
    }

}
