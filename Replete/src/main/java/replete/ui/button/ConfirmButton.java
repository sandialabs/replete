package replete.ui.button;

import javax.swing.event.ChangeListener;
import javax.swing.plaf.ColorUIResource;

import replete.event.ChangeNotifier;
import replete.threads.ThreadUtil;
import replete.ui.ColorLib;
import replete.ui.images.concepts.CommonConcepts;
import replete.ui.lay.Lay;
import replete.ui.windows.notifications.NotificationFrame;
import replete.ui.worker.RWorker;

/**
 * @author Derek Trumbo
 */

// Other possible features for this class:

// A configurable timeout to send "confirm" state back to "default" state
// to force users to confirm within a time period.

// Some way to prevent accidental double-clicking in "default" state causing
// the "confirm" state to be essentially skipped (a timeout after first
// click?).

public class ConfirmButton extends RButton {


    ///////////
    // ENUMS //
    ///////////

    private enum ConfirmButtonState {
        DEFAULT,
        CONFIRM,
        IN_PROGRESS
    }


    ////////////
    // FIELDS //
    ////////////

    private ConfirmButtonUiConfig defaultConfig;
    private ConfirmButtonUiConfig confirmConfig;
    private ConfirmButtonUiConfig inProgressConfig;
    private ConfirmButtonState state = ConfirmButtonState.DEFAULT;


    //////////////////
    // CONSTRUCTORS //
    //////////////////

    // Other constructors don't really make much sense for this button
    public ConfirmButton(ConfirmButtonUiConfig defaultConfig, ConfirmButtonUiConfig confirmConfig, ConfirmButtonUiConfig inProgressConfig) {
        this.defaultConfig = defaultConfig;
        this.confirmConfig = confirmConfig;
        this.inProgressConfig = inProgressConfig;
        updateConfig();
        addActionListener(e -> {
            if(state == ConfirmButtonState.DEFAULT) {
                state = ConfirmButtonState.CONFIRM;
                updateConfig();
            } else if(state == ConfirmButtonState.CONFIRM) {
                state = ConfirmButtonState.IN_PROGRESS;
                updateConfig();
                fireConfirmNotifier();
            }
            // If in "in progress" state, only reset() can send
            // it back to "default" state.
        });
    }


    //////////////
    // MUTATORS //
    //////////////

    public void reset() {
        state = ConfirmButtonState.DEFAULT;
        updateConfig();
    }

    public ConfirmButton setDefaultConfig(ConfirmButtonUiConfig defaultConfig) {
        this.defaultConfig = defaultConfig;
        updateConfig();
        return this;
    }
    public ConfirmButton setConfirmConfig(ConfirmButtonUiConfig confirmConfig) {
        this.confirmConfig = confirmConfig;
        updateConfig();
        return this;
    }
    public ConfirmButton setInProgressConfig(ConfirmButtonUiConfig inProgressConfig) {
        this.inProgressConfig = inProgressConfig;
        updateConfig();
        return this;
    }

    private void updateConfig() {
        ConfirmButtonUiConfig config;
        if(state == ConfirmButtonState.DEFAULT) {
            config = defaultConfig;
        } else if(state == ConfirmButtonState.CONFIRM) {
            config = confirmConfig;
        } else { //if(state == ConfirmButtonState.IN_PROGRESS)
            config = inProgressConfig;
        }

        if(config.getText() != null) {
            setText(config.getText());
        }
        if(config.getIcon() != null) {
            setIcon(config.getIcon());
        }
        if(config.getConcept() != null) {
            setIcon(config.getConcept());
        }
        if(config.getFg() != null) {
            setForeground(config.getFg());
        }
        if(config.getBg() != null) {
            setBackground(config.getBg());
        }
        if(config.getHints() != null) {
            Lay.hn(this, config.getHints());
        }
        boolean inProg = state == ConfirmButtonState.IN_PROGRESS;
        setEnabled(!inProg);
        setFocusPainted(!inProg);
    }


    ///////////////
    // NOTIFIERS //
    ///////////////

    private transient ChangeNotifier confirmNotifier = new ChangeNotifier(this);
    public void addConfirmListener(ChangeListener listener) {
        confirmNotifier.addListener(listener);
    }
    private void fireConfirmNotifier() {
        confirmNotifier.fireStateChanged();
    }


    //////////
    // TEST //
    //////////

    public static void main(String[] args) {
        final ConfirmButton a, b;
        NotificationFrame frame = new NotificationFrame("ConfirmButton Test");
        Lay.FLtg(frame,
            a = new ConfirmButton(
                new ConfirmButtonUiConfig()
                    .setText("&Quick...")
                    .setConcept(CommonConcepts.ACTION)
                    .setFg(null)
                    .setBg(new ColorUIResource(ColorLib.DEFAULT))
                    .setHints(null),
                new ConfirmButtonUiConfig()
                    .setText("&Confirm")
                    .setConcept(CommonConcepts.ACCEPT)
                    .setFg(null)
                    .setBg(ColorLib.YELLOW_LIGHT)
                    .setHints(null),
                new ConfirmButtonUiConfig()
                    .setText("Loading...")
                    .setConcept(CommonConcepts.REFRESH)
                    .setFg(null)
                    .setBg(null)
                    .setHints(null)
            ),
            "size=[600,200],center,visible"
        );

        a.addConfirmListener(e -> {
            RWorker<Void, Void> worker = new RWorker<Void, Void>() {
                @Override
                protected Void background(Void gathered) throws Exception {
                    ThreadUtil.sleep(3000);
                    return null;
                }
                @Override
                protected void complete() {
                    try {
                        getResult();
                        a.reset();
                    } catch(Exception e) {
                        e.printStackTrace();
                    }
                }
            };
            frame.addTaskAndExecuteFg("Test Task", worker);
        });
    }
}
