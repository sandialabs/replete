package finio.ui;

import java.io.File;
import java.lang.Thread.UncaughtExceptionHandler;
import java.util.ArrayList;
import java.util.List;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import finio.ui.app.AppContext;
import replete.event.ChangeNotifier;
import replete.ui.images.concepts.CommonConcepts;
import replete.ui.windows.Dialogs;
import replete.ui.windows.ExceptionDetails;
import replete.ui.windows.common.RWindowClosingEvent;
import replete.ui.windows.common.RWindowClosingListener;
import replete.ui.windows.notifications.NotificationFrame;
import replete.ui.windows.notifications.msg.NotificationError;

public class FFrameManager {


    ////////////
    // FIELDS //
    ////////////

    private static FFrameManager manager;
    private AppContext ac;
    private List<FFrame> frames = new ArrayList<>();    // Top-level windows


    ///////////////
    // SINGLETON //
    ///////////////

    public static FFrameManager getInstance() {
        if(manager == null) {
            manager = new FFrameManager();
            Thread.setDefaultUncaughtExceptionHandler(new UncaughtExceptionHandler() {
                public void uncaughtException(Thread t, Throwable e) {
                    NotificationError error = new NotificationError()
                        .setTitle("An unexpected error occurred.")
                        .setIcon(CommonConcepts.EXCEPTION)
                        .setError(e);
                    manager.getActiveWindow().getNotificationModel().getErrors().add(error);
                    manager.getActiveWindow().setShowNotificationArea(true);
                    File ws = new File("C:\\Users\\dtrumbo\\work\\eclipse-main");
                    Dialogs.showDetails(manager.getActiveWindow(),
                        new ExceptionDetails()
                            .setMessage("An unexpected error has occurred.")
                            .setTitle("Finio Error")
                            .setError(e)
                            .setInitiallyOpen(true)
                            .addSourceDir(new File(ws, "Cortext\\src"))
                            .addSourceDir(new File(ws, "Finio\\src"))
                            .addSourceDir(new File(ws, "Finio\\plugins"))
                            .addSourceDir(new File(ws, "Replete\\src"))
                            .addSourceDir(new File(ws, "RepleteExternals\\src"))
                            .addSourceDir(new File(ws, "Orbweaver\\src"))
                            .addSourceDir(new File(ws, "WebComms\\src"))
                    );
                }
            });
        }
        return manager;
    }


    /////////////////
    // CONSTRUCTOR //
    /////////////////

    private FFrameManager() {
        // Internal only
    }


    //////////////////////////
    // ACCESSORS / MUTATORS //
    //////////////////////////

    protected NotificationFrame getActiveWindow() {
        return frames.get(0);
    }

    public void setAppContext(AppContext ac) {
        this.ac = ac;
    }


    //////////
    // MISC //
    //////////

    public synchronized FFrame create() {
        final FFrame frame = new FFrame(ac);
        frame.addAttemptToCloseListener(new RWindowClosingListener() {
            public void stateChanged(RWindowClosingEvent e) {
//                ac.notImpl("Save");
//                if(!ac.getActionMap().checkSave(
//                        "Information on this frame is unsaved.  Do you wish to save?",
//                        "Save Before Closing?")) {
//                    e.cancelClose();
//                }
            }
        });
        frame.addClosingListener(new ChangeListener() {
            public void stateChanged(ChangeEvent e) {
                removeFrame(frame);
            }
        });
        frames.add(frame);
        return frame;
    }

    protected synchronized void removeFrame(FFrame frame) {
        frames.remove(frame);
        if(frames.isEmpty()) {
            fireAllFramesClosedNotifier();
        }
    }


    //////////////
    // NOTIFIER //
    //////////////

    private ChangeNotifier allFramesClosedNotifier = new ChangeNotifier(this);
    public void addAllFramesClosedListener(ChangeListener listener) {
        allFramesClosedNotifier.addListener(listener);
    }
    private void fireAllFramesClosedNotifier() {
        allFramesClosedNotifier.fireStateChanged();
    }
}
