package replete.util;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;

import replete.threads.ThreadUtil;

// This class is only a minor added convenience for developers.
// Whenever you want an action to run when a Java application
// exits you can just use:
//     Runtime.getRuntime().addShutdownHook(new Thread() {
//         @Override
//         public void run() {
//             // Stuff
//         }
//     });
// All registered shutdown hook threads will be run in parallel
// when the normal shutdown sequence begins.
//
// This class only exists as an experimental attempt to simplify
// the above code (not that it really needs it).  With this class
// you can just type:
//     ShutdownUtil.addAction(() -> { /*Stuff*/ });
// With options to remove actions after they've been registered
// and global enable/disable functionality.  Not sure how much
// of this is useful but it's just an initial attempt to investigate
// that.

public class ShutdownUtil {


    ////////////
    // FIELDS //
    ////////////

    private static Map<UUID, ShutdownAction> shutdownActions = new LinkedHashMap<>();
    private static ShutdownThread shutdownThread = new ShutdownThread();


    //////////////
    // MUTATORS //
    //////////////

    public static void setEnabled(boolean enable) {
        if(enable) {
            Runtime.getRuntime().addShutdownHook(shutdownThread);
        } else {
            Runtime.getRuntime().removeShutdownHook(shutdownThread);
        }
    }

    public static synchronized UUID addAction(ShutdownAction action) {
        UUID actionId = getActionId(action);
        if(actionId == null) {
            actionId = UUID.randomUUID();
        }
        shutdownActions.put(actionId, action);
        return actionId;
    }

    public static synchronized void removeAction(UUID actionId) {
        shutdownActions.remove(actionId);
    }

    public static synchronized void removeAction(ShutdownAction action) {
        UUID actionId = getActionId(action);
        if(actionId != null) {
            shutdownActions.remove(actionId);
        }
    }

    public static synchronized UUID getActionId(ShutdownAction action) {
        UUID foundId = null;
        for(UUID id : shutdownActions.keySet()) {
            ShutdownAction a = shutdownActions.get(id);
            if(a == action) {
                foundId = id;
            }
        }
        return foundId;
    }


    ///////////////////
    // INNER CLASSES //
    ///////////////////

    private static class ShutdownThread extends Thread {


        //////////////////
        // CONSTRUCTORS //
        //////////////////

        public ShutdownThread() {
            super("Shutdown Util Thread");
        }


        ////////////////
        // OVERRIDDEN //
        ////////////////

        @Override
        public void run() {
            synchronized(ShutdownUtil.class) {
                for(UUID actionId : shutdownActions.keySet()) {
                    try {
                        shutdownActions.get(actionId).perform();
                    } catch(Exception e) {
                        System.err.println("Shutdown Action Error");
                        System.err.println("=====================");
                        e.printStackTrace();         // Last ditch effort to see errors
                    }
                }
            }
        }
    }


    //////////
    // TEST //
    //////////

    public static void main(String[] args) {
        setEnabled(true);
        addAction(() -> {
            ThreadUtil.sleep(5000);
            System.out.println("Done5");
        });
        addAction(() -> {
            ThreadUtil.sleep(7000);
            System.out.println("Done7");
        });
    }
}
