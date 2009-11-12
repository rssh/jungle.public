package ua.gradsoft.caching.testclasses;

import ua.gradsoft.caching.CacheAction;
import ua.gradsoft.caching.annotations.Caching;
import ua.gradsoft.caching.util.AllArguments;

/**
 *Class to check caching
 * @author rssh
 */
@Caching
public class CachedClass1 implements InterfaceForTest1
{

    @Caching(cacheName="myCache",action=CacheAction.CACHE, keyBuilder=AllArguments.class)
    public int getX(int y)
    {
      ++nCalls;
      return y+2;
    }

    public int getNCalls()
    { return nCalls; }

    int nCalls = 0;

}
