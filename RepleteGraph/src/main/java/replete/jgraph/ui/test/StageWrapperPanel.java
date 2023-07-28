package replete.jgraph.ui.test;

import java.awt.BorderLayout;

import javax.swing.JPanel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import replete.event.ChangeNotifier;
import replete.event.ProgressEvent;
import replete.event.ProgressListener;
import replete.jgraph.test.StageWrapper;
import replete.pipeline.Stage;
import replete.pipeline.events.ParameterChangeEvent;
import replete.pipeline.events.ParameterChangeListener;
import replete.ui.GuiUtil;
import replete.ui.lay.Lay;
import replete.ui.lay.LayHints;

public class StageWrapperPanel extends JPanel {


    ////////////
    // FIELDS //
    ////////////

    // Core

    public StageWrapper wrapper;
    public Stage stage;

    // UI

    private StageWrapperTitlePanel pnlTitle;
    private StageWrapperStatusPanel pnlStatus;
    private JPanel pnlBodyContainer;
    private JPanel pnlLower;

    // Other

    private boolean collapsed = false;


    /////////////////
    // CONSTRUCTOR //
    /////////////////

    public StageWrapperPanel(StageWrapper wrapper, StageParameterPanel pnlParams) {
        this.wrapper = wrapper;
        stage = wrapper.getStage();

        if(pnlParams == null) {
            pnlParams = new EmptyStageParameterPanel(wrapper);
        }

        LayHints.addGlobalHints("nodebug");
        Lay.BLtg(this,
            "C", pnlBodyContainer = Lay.BL(
                "N", Lay.hn(pnlTitle = new StageWrapperTitlePanel(stage.getName())),
                "C", pnlLower = Lay.BL(
                    "C", pnlParams,
                    "S", Lay.hn(pnlStatus = new StageWrapperStatusPanel())
                ),
                "mb=[2,black],cursor=default"
            ),
            "eb=1lt,opaque=false"
        );

        pnlTitle.addCollapseExpandListener(new ChangeListener() {
            public void stateChanged(ChangeEvent e) {
                System.out.println(getMinimumSize());
                setCollapsed(!collapsed);
            }
        });

        if(stage.isDirty()) {
            pnlStatus.setDirty();
        } else {
            pnlStatus.setComplete();
        }

        stage.addStartListener(new ChangeListener() {
            public void stateChanged(ChangeEvent e) {
                GuiUtil.safeSync(new Runnable() {
                    public void run() {
                        pnlStatus.setProgress();
                    }
                });
            }
        });
        stage.addCompleteListener(new ChangeListener() {
            public void stateChanged(ChangeEvent e) {
                GuiUtil.safeSync(new Runnable() {
                    public void run() {
                        pnlStatus.setComplete();
                    }
                });
            }
        });
        stage.addProgressListener(new ProgressListener() {
            public void stateChanged(final ProgressEvent e) {
                GuiUtil.safeSync(new Runnable() {
                    public void run() {
                        pnlStatus.updateProgress(e.getMessage());
                    }
                });
            }
        });
        stage.addDirtyListener(new ChangeListener() {
            public void stateChanged(ChangeEvent e) {
                GuiUtil.safeSync(new Runnable() {
                    public void run() {
                        if(stage.isDirty()) {
                            pnlStatus.setDirty();
                        }
                    }
                });
            }
        });
        stage.addParameterChangeListener(new ParameterChangeListener() {
            public void stateChanged(ParameterChangeEvent e) {
                // TODO: make sure dirty is set!!! (in the framework layer!!!)
            }
        });
    }


    ///////////////
    // NOTIFIERS //
    ///////////////

    private ChangeNotifier collapseExpandNotifier = new ChangeNotifier(this);
    public void addCollapseExpandListener(ChangeListener listener) {
        collapseExpandNotifier.addListener(listener);
    }
    private void fireCollapseExpandNotifier() {
        collapseExpandNotifier.fireStateChanged();
    }


    //////////////////////////
    // ACCESSORS / MUTATORS //
    //////////////////////////

    // Accessor

    public boolean isSelected() {
        return pnlTitle.isSelected();
    }
    public boolean isCollapsed() {
        return collapsed;
    }

    // Mutator

    public void setSelected(boolean selected) {
        pnlTitle.setSelected(selected);
    }
    public void setCollapsed(boolean collapsed) {
        if(collapsed != this.collapsed) {
            this.collapsed = collapsed;
            if(collapsed) {
                pnlBodyContainer.remove(pnlLower);
            } else {
                pnlBodyContainer.add(pnlLower, BorderLayout.CENTER);
            }
            pnlBodyContainer.updateUI();
            pnlTitle.setCollapsed(collapsed);
            fireCollapseExpandNotifier();
        }
    }
}
