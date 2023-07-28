package replete.ui;

import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.Timer;

// It's possible that swing timers can send events in their action listeners
// that keep the event queue thread alive.  Like calling repaint() on some
// long-dead panel once a second can keep the EDT alive because it waits
// about 1-2 seconds and then quits.  Swing Timers operating at more like 7
// seconds per iteration do not cause this behavior.  This timer rather
// brute force decides if the app is closing and stops itself.

public class TrueDaemonTimer extends Timer implements ActionListener {

    private ActionListener listener;

    public TrueDaemonTimer(int delay, ActionListener listener) {
        super(delay, null);
        this.listener = listener;
        addActionListener(this);
    }

    public TrueDaemonTimer startTimer() {
        super.start();
        return this;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        boolean allOff = true;
        for(Window w : Window.getWindows()) {
            if(w.isVisible()) {
                allOff = false;
            }
        }
        if(allOff) {
            stop();
        } else {
            listener.actionPerformed(e);
        }
    }
}
