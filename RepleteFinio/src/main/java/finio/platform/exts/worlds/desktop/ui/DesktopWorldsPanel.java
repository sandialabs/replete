package finio.platform.exts.worlds.desktop.ui;

import javax.swing.JDesktopPane;
import javax.swing.event.InternalFrameAdapter;
import javax.swing.event.InternalFrameEvent;

import finio.ui.actions.FActionMap;
import finio.ui.app.AppContext;
import finio.ui.world.WorldPanel;
import finio.ui.worlds.WorldContext;
import finio.ui.worlds.WorldsPanel;
import replete.ui.lay.Lay;

public class DesktopWorldsPanel extends WorldsPanel {


    ////////////
    // FIELDS //
    ////////////

    private FActionMap actionMap;
    private JDesktopPane desktopPane;


    /////////////////
    // CONSTRUCTOR //
    /////////////////

    public DesktopWorldsPanel(final AppContext ac, final FActionMap actionMap) {
        super(ac);
        this.actionMap = actionMap;

        Lay.BLtg(this,
            "C", desktopPane = Lay.DP("opaque=false"),  // TODO Put in scroll pane some time in the future...
            WorldsPanel.STYLE
        );

        // Add all of the worlds that exist right now.
        int i = 0;
        for(WorldContext w : ac.getWorlds()) {
            addWorld(w, i++);
        }

        ac.addWorldAddedListener(worldAddedListener);
    }

    @Override
    public void unsubscribe() {
        ac.removeWorldAddedListener(worldAddedListener);
    }


    ////////////////
    // OVERRIDDEN //
    ////////////////

    @Override
    public void setSelectedIndex(int i) {
        // TODO
    }

    @Override
    public void addWorld(final WorldContext wc, int i) {
        WorldPanel pnlWorld = wc.getWorldPanel();

        WorldInternalFrame frame = new WorldInternalFrame(wc, i);
        frame.addInternalFrameListener(new InternalFrameAdapter() {
            @Override
            public void internalFrameActivated(InternalFrameEvent e) {
                fireWorldSelectedNotifier(ac.getWorldIndex(wc));
            }
        });

        desktopPane.add(frame);
        frame.setVisible(true);
        frame.moveToFront();
        pnlWorld.init();
    }

    @Override
    public void removeWorld(WorldContext wc) {
        // TODO
    }
}
