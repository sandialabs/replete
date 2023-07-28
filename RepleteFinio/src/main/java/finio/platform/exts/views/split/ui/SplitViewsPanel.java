package finio.platform.exts.views.split.ui;

import java.util.List;

import javax.swing.JComponent;
import javax.swing.JSplitPane;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import finio.appstate.AppStateChangeEvent;
import finio.appstate.AppStateChangeListener;
import finio.plugins.extpoints.Views;
import finio.ui.TitlePanel;
import finio.ui.app.AppContext;
import finio.ui.view.ViewContainerPanel;
import finio.ui.view.ViewPanel;
import finio.ui.views.ViewSelectionListener;
import finio.ui.views.ViewsPanel;
import finio.ui.worlds.WorldContext;
import replete.ui.images.concepts.CommonConcepts;
import replete.ui.images.concepts.ImageLib;
import replete.ui.lay.Lay;


public class SplitViewsPanel extends ViewsPanel {


    //////////
    // ENUM //
    //////////

    private enum Layout {
        LEFT,
        BOTH,
        RIGHT
    }


    ////////////
    // FIELDS //
    ////////////

    private Layout layout = Layout.LEFT;   // Bring back use of TitlePanel
    private AppContext ac;
    private WorldContext wc;
    private JSplitPane splMain;
    private ViewPanel pnlLeft;
    private ViewPanel pnlRight;
    private JComponent cmpCenter;
    private Views views;


    /////////////////
    // CONSTRUCTOR //
    /////////////////

    public SplitViewsPanel(final AppContext ac, final WorldContext wc, Views views) {
        this.ac = ac;
        this.wc = wc;
        this.views = views;

        Lay.BLtg(this);

        wc.getWorldPanel().addViewAddedListener(new ChangeListener() {
            public void stateChanged(ChangeEvent e) {
                List<ViewContainerPanel> views = wc.getWorldPanel().getViewPanels();
                if(views.size() == 1) {
                    pnlLeft = views.get(0).getViewPanel();
                    center(pnlLeft);
                    pnlLeft.focus();
                    layout = Layout.LEFT;

                } else if(views.size() == 2) {
                    pnlRight = views.get(1).getViewPanel();
                    int state = ac.getConfig().getSplitPaneState();
                    if(state != 1 && state != 2) {
                        ac.getConfig().setSplitPaneState(1);
                    }
                }
            }
        });

        ac.getConfig().addPropertyChangeListener(new AppStateChangeListener() {
            public void stateChanged(AppStateChangeEvent e) {
                if(e.getName().equals("splitPaneState")) {
                    switch(ac.getConfig().getSplitPaneState()) {
                        case 0: showLeftOnly();  break;
                        case 1: showBothVert();  break;
                        case 2: showBothHoriz(); break;
                        case 3: showRightOnly(); break;
                    }
                }
            }
        });
    }


    /////////////////
    // VIEW CHANGE //
    /////////////////

    public void showLeftOnly() {
        if(pnlLeft == null) {
            return;
        }
        center(pnlLeft);
        pnlLeft.focus();
        layout = Layout.LEFT;
    }
    public void showBothVert() {
        if(pnlLeft == null || pnlRight == null) {
            return;
        }
        split(JSplitPane.TOP, JSplitPane.BOTTOM);
        center(splMain);
        if(layout == Layout.LEFT) {
            pnlRight.focus();
        } else {
            pnlLeft.focus();
        }
        layout = Layout.BOTH;
    }
    public void showBothHoriz() {
        if(pnlLeft == null || pnlRight == null) {
            return;
        }
        split(JSplitPane.LEFT, JSplitPane.RIGHT);
        center(splMain);
        if(layout == Layout.LEFT) {
            pnlRight.focus();
        } else {
            pnlLeft.focus();
        }
        layout = Layout.BOTH;
    }
    public void showRightOnly() {
        if(pnlRight == null) {
            return;
        }
        center(pnlRight);
        pnlRight.focus();
        layout = Layout.RIGHT;
    }
    private void split(String loc1, String loc2) {
        if(splMain == null) {
            splMain = Lay.SPL();
        }
        splMain.removeAll();
        splMain.setOrientation(loc1.equals(JSplitPane.TOP) ?
            JSplitPane.VERTICAL_SPLIT :
                JSplitPane.HORIZONTAL_SPLIT);
        splMain.add(pnlLeft, loc1);
        splMain.add(pnlRight, loc2);
        splMain.setDividerLocation(loc1.equals(JSplitPane.TOP) ? 500 : getWidth() / 2);
        splMain.updateUI();
    }
    private void center(JComponent cmpNewCenter) {
        if(cmpCenter != null) {
            remove(cmpCenter);
        }
        Lay.BLtg(this,
            "N", new TitlePanel("asdfsfd", ImageLib.get(CommonConcepts.CANCEL)),
            "C", cmpCenter = cmpNewCenter
        );
        updateUI();
    }


    //////////
    // MISC //
    //////////

    @Override
    public void init() {
        if(splMain != null) {
            splMain.setDividerLocation(525);
        }
    }


    @Override
    public void addViewSelectionListener(ViewSelectionListener listener) {
    }


    @Override
    public void removeViewSelectionListener(ViewSelectionListener listener) {
    }
}
