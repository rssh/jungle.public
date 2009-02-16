
package ua.gradsoft.caching.testclasses;

import ua.gradsoft.caching.CacheAction;
import ua.gradsoft.caching.annotations.Caching;
import ua.gradsoft.caching.util.AllArguments;

/**
 *
 * @author rssh
 */
public interface InterfaceForTest2 {

    @Caching(cacheName="qqq",action=CacheAction.CACHE,keyBuilder=AllArguments.class)
    public String getXS(String v1, String v2);

    public int getNCalls();
}
