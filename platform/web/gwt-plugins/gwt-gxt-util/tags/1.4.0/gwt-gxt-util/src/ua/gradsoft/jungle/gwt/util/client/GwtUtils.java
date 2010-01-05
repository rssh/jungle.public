
package ua.gradsoft.jungle.gwt.util.client;

/**
 *General Gwt Utils
 * @author rssh
 */
public class GwtUtils {


    public static Resolution getCurrentResolution()
    { return resolution1024_768; }

    private static class Resolution1024_768 implements Resolution
    {

        public int getWWidth() {
           return W_WIDTH;
        }

        public int getXHeight() {
           return X_HEIGHT;
        }

        public int getXWidth() {
            return X_WIDTH;
        }

       static final int X_WIDTH=15;
       static final int W_WIDTH=18;
       static final int X_HEIGHT=22;

    }

    private final static Resolution resolution1024_768 = new Resolution1024_768();


}
