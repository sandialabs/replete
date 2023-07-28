package replete.ui.multic;

import java.io.File;
import java.lang.Thread.UncaughtExceptionHandler;

import replete.pstate2.PersistentStateLoadException;
import replete.pstate2.PersistentStateManager;
import replete.pstate2.XmlFileManager;
import replete.ui.windows.Dialogs;
import replete.ui.windows.common.RWindowClosingEvent;
import replete.ui.windows.common.RWindowClosingListener;
import replete.util.Application;
import replete.util.User;


public class MultiConsoles {

    public static PersistentStateManager stateMgr = new XmlFileManager(new File(User.getHome(), ".multiconsoles"));
    private static MultiConsolesFrame frame;

    public static void main(String[] args) {
        Application.setName("Multi Consoles");
        Application.setVersion("0.1");

        AppState state;
        try {
            state = (AppState) stateMgr.load();
        } catch(PersistentStateLoadException e) {
            state = new AppState();
        }
        AppState.setState(state);

        //LafManager.initialize(state.getLafClassName(), state.getLafThemeName());

        frame = new MultiConsolesFrame();
        frame.setVisible(true);
        Thread.setDefaultUncaughtExceptionHandler(new UncaughtExceptionHandler() {
            public void uncaughtException(Thread e, Throwable t) {
                Dialogs.showDetails(frame, "An unexpected error has occurred.", "Error", t);
            }
        });

        // Don't have progress window installed yet... Should be accessible via LafManager probably.
        /*LafManager.setNeedToRebootListener(new RebootFramesListener() {
            public void reboot() {
                frame.saveState();
                frame.dispose();
                frame = new MultiConsolesFrame();
                frame.setVisible(true);
            }
            public boolean allowReboot() {
                return !frame.isProcessRunning();
            }
        });*/

        frame.addAttemptToCloseListener(new RWindowClosingListener() {
            public void stateChanged(RWindowClosingEvent e) {
                if(!frame.stopProcesses()) {
                    e.cancelClose();
                }
            }
        });

        Runtime.getRuntime().addShutdownHook(new Thread() {
            @Override
            public void run() {
                frame.stopProcesses();
                saveState();
            }
        });

//        attemptAddMacShutdownHook();
//        addApplicationListener(new ApplicationAdapter() {
//            @Override
//            public void handleQuit(ApplicationEvent arg0) {
//                frame.closeFrame();
//            }
//        });
    }

//    private static Object macApp = null;
//    private static void attemptAddMacShutdownHook() {
//        try {
//            Class<?> c = Class.forName("com.apple.eawt.Application");
//            macApp = c.newInstance();
//            Class<?> adapClass = Class.forName("com.apple.eawt.ApplicationAdapter");
//            c.getMethod("addApplicationListener", new Class[]{adapClass});
//
//        }
//    }

    private static void saveState() {
        frame.saveState();
        AppState state = AppState.getState();
//        state.setLafClassName(LafManager.getCurrentLaf().getCls());
//        state.setLafThemeName(LafManager.getCurrentLaf().getCurTheme());
        stateMgr.save(AppState.getState());
    }
}
