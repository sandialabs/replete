package replete.threads;

import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.Timer;

// Memory leak issue:
//   This class does currently maintain a global list of timers.
//   An issue can occur if the timers' action listeners have
//   references back to their enclosing classes via this$0
//   shadow references.  If the timer is removed, however,
//   then it cannot make use of the global swing timer
//   manager functionality.  Perhaps this issue has only
//   highlighted that individual panels should control the
//   lifecycles of their timers - another example of how
//   global/static memory can hurt overall software quality
//   in the long run. {artf193423}

public class SwingTimerManager {
    private static List<Timer> timers = new ArrayList<>();

    public synchronized static Timer create(int delay, ActionListener listener) {
        return create(delay, true, listener);    // Take Swing default for repeats
    }
    public synchronized static Timer create(int delay, boolean repeats, ActionListener listener) {
        Timer timer = new Timer(delay, listener);
        timer.setRepeats(repeats);
        timers.add(timer);
        return timer;
    }

    public synchronized static void remove(Timer t) {
        if(t.isRunning()) {   // Could be optional
            t.stop();
        }
        timers.remove(t);
    }

    public synchronized static void shutdown() {
        for(Timer timer : timers) {
            timer.stop();
        }
    }

    private static class TimerWrapper {
        Timer timer;
    }
    private static Map<String, TimerWrapper> actions = new HashMap<>();
    public static void delayedAction(ActionDescriptor request) {
        synchronized(actions) {
            TimerWrapper act = actions.get(request.id);
            boolean firstTime;
            if(act == null) {
                act = new TimerWrapper();
                actions.put(request.id, act);
                firstTime = true;
            } else {
                firstTime = false;
            }
            if(request.immediateAction != null &&
                    (firstTime || (act.timer != null &&
                    (!act.timer.isRunning() || request.repeatImmediateAction)))) {
                request.immediateAction.perform();
            }
            if(request.delayedAction != null) {
                if(request.delay < 0) {
                    request.delayedAction.perform();
                } else {
                    if(act.timer != null) {
                        act.timer.stop();
                    }
                    act.timer = create(request.delay, false, e -> request.delayedAction.perform());
                    act.timer.start();
                }
            }
        }
    }

}
