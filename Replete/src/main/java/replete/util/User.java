package replete.util;

import java.io.File;

/**
 * @author Derek Trumbo
 */

public class User {

    public static String getName() {
        return System.getProperty("user.name");
    }

    public static File getHome() {
        return getHome(null);
    }
    public static File getHome(String fileName) {
        File home = new File(System.getProperty("user.home"));
        if(!home.exists()) {
            return null;
        }
        if(fileName != null) {
            return new File(home, fileName);
        }
        return home;
    }

    public static File getDesktop() {
        return getDesktop(null);
    }
    public static File getDesktop(String fileName) {
        File home = new File(System.getProperty("user.home"));
        File dskt = new File(home, "Desktop");
        if(!dskt.exists()) {
            return null;
        }
        if(fileName != null) {
            return new File(dskt, fileName);
        }
        return dskt;
    }
}
