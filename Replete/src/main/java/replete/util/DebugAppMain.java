package replete.util;

import java.util.Arrays;

import replete.SoftwareVersion;
import replete.errors.ExceptionUtil;
import replete.io.FileUtil;
import replete.threads.ThreadUtil;

// This class simply exists to be a "fake" entry
// point to an application.  This can be useful
// for testing the Replete Extensibility Platform,
// providing a safe and small  application to test
// that the right main is being invoked and with
// the right arguments.

public class DebugAppMain {


    //////////
    // MAIN //
    //////////

    private static String[] replaceWithTestArgs(String[] args) {
        args = new String[] {
//            "sleep5000",
//            "toss",
//            "hooktask"
        };
        return args;
    }

    public static void main(String[] args) {
        if(SoftwareVersion.get().isDevelopment()) {
            args = replaceWithTestArgs(args);
        }

        System.out.println(DebugAppMain.class.getSimpleName() + " Invoked With " + args.length + " Args: " + Arrays.toString(args));
        for(int i = 1; i <= 10; i++) {
            for(int j = 1; j <= i; j++) {
                System.out.print("*");
            }
            System.out.println();
        }
        boolean hookTask = false;
        boolean toss = false;
        Integer sleep = null;
        for(int a = 0; a < args.length; a++) {
            if(args[a].equalsIgnoreCase("toss")) {
                toss = true;
            } else if(args[a].startsWith("sleep")) {
                String s = args[a].substring("sleep".length());
                sleep = Integer.parseInt(s);
            } else if(args[a].equalsIgnoreCase("hooktask")) {
                hookTask = true;
            }
        }

        if(hookTask) {
            ShutdownUtil.setEnabled(true);
            ShutdownUtil.addAction(() -> {
                System.out.println("-- Debug Shutdown Action --");
                FileUtil.writeTextContent(User.getDesktop("shutdown-file.txt"), DateUtil.toLongString(System.currentTimeMillis()));
            });
        }
        if(sleep != null) {
            ThreadUtil.sleep(sleep);
        }
        if(toss) {
            ExceptionUtil.toss("DebugMain Exception");
        }
    }

}
