package replete.util;

import java.io.File;
import java.net.URL;
import java.security.ProtectionDomain;

import replete.errors.RuntimeConvertedException;

public class CodeUtil {

    public static File getCodeSourcePath() {
        Class<?> previous = ClassUtil.getCallingClass(CodeUtil.class);
        try {
            ProtectionDomain domain = previous.getProtectionDomain();
            URL location = domain.getCodeSource().getLocation();
            return new File(location.toURI().getPath());
        } catch(Exception e) {
            throw new RuntimeConvertedException(e);
        }
    }

    //http://stackoverflow.com/questions/320542/how-to-get-the-path-of-a-running-jar-file
    /*//%20 for spaces
    File f = new File(PluginManager.class.getProtectionDomain().getCodeSource().getLocation().getPath());
    System.out.println(f);
    File f2 = null;
    try {
        f2 = new File(PluginManager.class.getProtectionDomain().getCodeSource().getLocation().toURI().getPath());
        System.out.println(f2);
    } catch(URISyntaxException e) {
        e.printStackTrace();
    }
    // doesn't include *.jar
    String f3 = ClassLoader.getSystemClassLoader().getResource(".").getPath();
    System.out.println(f3);

    // Starts with / (could be fine)
    String path = PluginManager.class.getProtectionDomain().getCodeSource().getLocation().getPath();
    String decodedPath = null;
    try {
        decodedPath = URLDecoder.decode(path, "UTF-8");
        System.out.println(decodedPath);
    } catch(UnsupportedEncodingException e) {
        e.printStackTrace();
    }
    Dialogs.showMessage(f.getAbsolutePath() + "\n" + f2.getAbsolutePath() + "\n" + f3 + "\n" + decodedPath);*/
}
