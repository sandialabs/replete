package replete.ui;

import java.awt.Component;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.HierarchyEvent;
import java.awt.event.HierarchyListener;

import javax.swing.JLabel;
import javax.swing.JSplitPane;

import replete.ui.lay.Lay;
import replete.ui.panels.SelectionState;

public class RSplitPane extends JSplitPane implements SelectionStateSavable {


    ///////////
    // ENUMS //
    ///////////

    public enum SplitPaneSelectionStateCreationMethod implements SelectionStateCreationMethod {
        RECORD_BOTH,               // (Default) Record split pane's own div location & both components
        RECORD_LEFT_TOP_ONLY,      // Record split pane's own div location & left/top component only
        RECORD_RIGHT_BOTTOM_ONLY,  // Record split pane's own div location & right/bottom component only
        RECORD_SELF_ONLY           // Only record split pane's own div location
    }


    //////////////////
    // CONSTRUCTORS //
    //////////////////

    public RSplitPane() {}
    public RSplitPane(int newOrientation) {
        super(newOrientation);
    }
    public RSplitPane(int newOrientation, boolean newContinuousLayout) {
        super(newOrientation, newContinuousLayout);
    }
    public RSplitPane(int newOrientation, Component newLeftComponent, Component newRightComponent) {
        super(newOrientation, newLeftComponent, newRightComponent);
    }
    public RSplitPane(int newOrientation, boolean newContinuousLayout, Component newLeftComponent,
                      Component newRightComponent) {
        super(newOrientation, newContinuousLayout, newLeftComponent, newRightComponent);
    }

// http://stackoverflow.com/questions/1879091/jsplitpane-setdividerlocation-problem
// Didn't work as intended for me...
//    private boolean hasProportionalLocation = false;
//    private double proportionalLocation = 0.5;
//    private boolean isPainted = false;
//
//    @Override
//    public void setDividerLocation(double proportionalLocation) {
//        if (!isPainted) {
//            hasProportionalLocation = true;
//            this.proportionalLocation = proportionalLocation;
//        } else {
//            super.setDividerLocation(proportionalLocation);
//        }
//    }
//
//    @Override
//    public void paint(Graphics g) {
//        if (!isPainted) {
//            if (hasProportionalLocation) {
//                super.setDividerLocation(proportionalLocation);
//            }
//            isPainted = true;
//        }
//        super.paint(g);
//    }


    ////////////////
    // OVERRIDDEN //
    ////////////////

    // https://gist.github.com/daveray/1021984
    // Still causes momentary flicker...
    @Override
    public void setDividerLocation(final double proportion) {
        if(isShowing()) {
            if(getWidth() > 0 && getHeight() > 0) {
                super.setDividerLocation(proportion);
            } else {
                addComponentListener(new ComponentAdapter() {
                    @Override
                    public void componentResized(ComponentEvent ce) {
                        removeComponentListener(this);
                        setDividerLocation(proportion);
                    }
                });
            }
        } else {
            addHierarchyListener(new HierarchyListener() {
                @Override
                public void hierarchyChanged(HierarchyEvent e) {
                    if((e.getChangeFlags() & HierarchyEvent.SHOWING_CHANGED) != 0 && isShowing()) {
                        removeHierarchyListener(this);
                        setDividerLocation(proportion);
                    }
                }
            });
        }
    }

    // NOTE: This component by default calls the left (top) and right (bottom)
    // components' DEFAULT (no-argument) getSelectionState method.
    // Thus, if you need a non-default strategy for those components'
    // selection state creation (e.g. TreeSelectionStateIdentityMethod),
    // then you must record those components' selection state yourself
    // in the code that is calling this method.  If you want to eliminate
    // redundant calls to getSelectionState, then use the custom method
    // (SplitPaneSelectionStateIdentityMethod.?) to change which contained
    // components should not have their selection state recorded by the
    // method below.
    @Override
    public SelectionState getSelectionState(Object... args) {
        SplitPaneSelectionStateCreationMethod method =
            getDefaultArg(args, SplitPaneSelectionStateCreationMethod.RECORD_BOTH);

        SelectionState state = new SelectionState()
            .p("divLoc", getDividerLocation())
        ;

        if(method == SplitPaneSelectionStateCreationMethod.RECORD_BOTH ||
                method == SplitPaneSelectionStateCreationMethod.RECORD_LEFT_TOP_ONLY) {
            state.putSsIf("firstComp", getLeftComponent());    // Also handles "top" component

        } else if(method == SplitPaneSelectionStateCreationMethod.RECORD_BOTH ||
                method == SplitPaneSelectionStateCreationMethod.RECORD_RIGHT_BOTTOM_ONLY) {
            state.putSsIf("secondComp", getRightComponent());  // Also handles "bottom" component
        }

        return state;
    }

    @Override
    public void setSelectionState(SelectionState state) {
        setDividerLocation(state.getGx("divLoc"));
        state.setSsIf(getLeftComponent(), "firstComp");
        state.setSsIf(getRightComponent(), "secondComp");
    }


    //////////
    // TEST //
    //////////

    public static void main(String[] args) {

        RSplitPane x = new  RSplitPane(
            JSplitPane.HORIZONTAL_SPLIT,
            new JLabel("test"), new JLabel("test2"));
        x.setDividerLocation(0.5);

        Lay.BLtg(Lay.fr("asdfs"),
            "C", x,
            "size=600,center,visible"
        );
    }
}
