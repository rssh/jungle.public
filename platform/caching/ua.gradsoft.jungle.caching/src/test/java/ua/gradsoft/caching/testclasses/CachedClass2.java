package ua.gradsoft.caching.testclasses;

public class CachedClass2 implements InterfaceForTest2
{

    public int getNCalls() {
        return nCalls;
    }

    public String getXS(String v1, String v2) {
        ++nCalls;
        return v1+v2;
    }

    int nCalls = 0;

}
