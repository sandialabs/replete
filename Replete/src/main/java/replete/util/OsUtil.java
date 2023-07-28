package replete.util;

import java.awt.Desktop;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

import replete.process.ProcessUtil;

/**
 * Convenience methods for certain operating system information.
 *
 * @author Derek Trumbo
 */

public class OsUtil {
    public static boolean isWindows() {
        return System.getProperty("os.name").indexOf("Windows") != -1;
    }
    public static boolean isMac() {
        return System.getProperty("os.name").indexOf("Mac") != -1;
    }
    public static boolean isLinux() {
        return System.getProperty("os.name").indexOf("Linux") != -1;
    }
    public static boolean isSun() {
        return System.getProperty("os.name").indexOf("Sun") != -1;
    }
    public static boolean is64Bit() {
        return System.getProperty("os.arch").contains("64");
    }

    public static void openExplorer(File file) {
        openExplorer(file, !file.isDirectory());
    }
    public static void openExplorer(File file, boolean select) {
        try {
            if(isWindows()) {
                String sel = select ? "/select, " : "";
                Runtime.getRuntime().exec("explorer " + sel + file.getAbsolutePath());
            } else if(isMac()) {
                Runtime.getRuntime().exec(new String[] {"open", "-R", file.getAbsolutePath()});
            }
        } catch(IOException e) {
            throw new RuntimeException("Could not open explorer window.", e);
        }
    }

    public static void openTerminal(File file) {
        try {
            if(isWindows()) {
                Runtime.getRuntime().exec("cmd /c start cmd", null, file);
            } else if(isMac()) {
                //?
            }
        } catch(IOException e) {
            throw new RuntimeException("Could not open terminal window.", e);
        }
    }

    public static void openSystemEditor(File F) {
        File workingDir = F;
        if(workingDir.isFile()) {
            File parentFile = workingDir.getParentFile();
            if(parentFile != null) {
                workingDir = parentFile;
            }
        }
        try {
            if(OsUtil.isWindows()) {
                Runtime.getRuntime().exec("cmd /c start \"\" \"" + F.getAbsolutePath() + "\"", null, workingDir);
            } else if(OsUtil.isMac()) {
                Runtime.getRuntime().exec("open \"" + F.getAbsolutePath() + "\"", null, workingDir);
            }
        } catch(IOException e1) {
            e1.printStackTrace();
        }
    }

    public static void openSystemBrowser(String url) {
        try {
            if(OsUtil.isWindows()) {
                Runtime.getRuntime().exec("cmd /c start \"\" \"" + url + "\"", null, null);
            } else if(OsUtil.isMac()) {
                Runtime.getRuntime().exec("open \"" + url + "\"", null, null);
            }
        } catch(IOException e1) {
            e1.printStackTrace();
        }
    }

    public static void openWebpage(URI uri) {
        Desktop desktop = Desktop.isDesktopSupported() ? Desktop.getDesktop() : null;
        if (desktop != null && desktop.isSupported(Desktop.Action.BROWSE)) {
            try {
                desktop.browse(uri);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
    public static void openWebpage(URL url) {
        try {
            openWebpage(url.toURI());
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
    }

    public static String getHostName() {
        try {
            String[] lines = ProcessUtil.getOutput("hostname");
            return lines[0].trim();
        } catch(Exception e) {
            return null;
        }
    }

    // This seems like the wrong way to do this.  Internet search
    // shows people using any way EXCEPT this way to do it correctly.
    // http://stackoverflow.com/questions/7348711/recommended-way-to-get-hostname-in-java
    // http://stackoverflow.com/questions/34842698/inetaddress-getcanonicalhostname-returns-ip-instead-of-hostname
//    private String findHostName() {
//        try {
//            Enumeration<NetworkInterface> ifcs = NetworkInterface.getNetworkInterfaces();
//            MembershipMap<String, Boolean> mm = new MembershipMap<>();
//            while(ifcs.hasMoreElements()) {
//                NetworkInterface ifc = ifcs.nextElement();
//                Enumeration<InetAddress> addrs = ifc.getInetAddresses();
//                while(addrs.hasMoreElements()) {
//                    InetAddress addr = addrs.nextElement();              // One of these two lines can hang
//                    mm.addMembership(addr.getCanonicalHostName(), true); // One of these two lines can hang
//                }
//            }
//            int maxCount = -1;
//            String maxGroup = null;
//            for(String group : mm.keySet()) {
//                int count = mm.getMemberships(group, true);
//                if(count > maxCount) {
//                    maxGroup = group;
//                    maxCount = count;
//                }
//            }
//            if(maxGroup == null || maxGroup.equals("")) {
//                maxGroup = NodeSummaryState.UNKNOWN_NAME;
//            }
//            return maxGroup;
//        } catch (Exception e) {
//            return NodeSummaryState.UNKNOWN_NAME;
//        }
//    }


    //////////
    // TEST //
    //////////

    public static void main(String[] args) {
        System.out.println(getHostName());
    }

// Not sure what this was for...
//
//  Runtime.getRuntime().exec("explorer /select, " + String path);
//  Runtime.getRuntime().exec("cmd /c start cmd", null, new File(path));
//  Runtime.getRuntime().exec(new String[] {"open", "-R", path});
//  then clean up process, waitFor, if procexit val not zero, read all output, destroy
//
//  String TERMINAL_SCRIPT = "";
//  tell application "Terminal"
//      activate
//      if (the (count of the window) = 0) or (the busy of window 1 = true) then
//          tell application "System Events"
//              keystroke "n" using command down
//          end tell
//      end if
//      do script "cd '$THEPATH$'" in window 1
//  end tell
//
//  String[] cmdArray = {"osascript", "-e", TERMINAL_SCRIPT.replaceFirst("\\$THEPATH\\$", path)};
//  Runtime.getRuntime().exec(cmdArray);
//   For the terminal/Command Prompt:
//  File file = new File(path);
//  if(file.isFile()) {
//      File parentFile = file.getParentFile();
//      if(parentFile != null) {
//          path = parentFile.getAbsolutePath();
//      }
//  }

}
