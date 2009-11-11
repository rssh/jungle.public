
package ua.gradsoft.caching;

import java.io.Serializable;

/**
 * interfacem which hold information: how to store object in cache.
 */
public interface ValueTransformer<OT,CT extends Serializable>
{

    public CT toCache(OT ot);

    public OT fromCache(CT ct);

}
