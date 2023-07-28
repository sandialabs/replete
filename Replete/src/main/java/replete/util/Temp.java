package replete.util;

import java.io.File;

/**
 * @author Derek Trumbo
 */

public class Temp {
    public static File get() {
        return get(null);
    }
    public static File get(String fileName) {
        File tmpDir = new File(System.getProperty("java.io.tmpdir"));
        if(!tmpDir.exists()) {
            return null;
        }
        if(fileName != null) {
            return new File(tmpDir, fileName);
        }
        return tmpDir;
    }
}
