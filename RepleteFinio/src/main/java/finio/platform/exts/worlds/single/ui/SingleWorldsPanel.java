package finio.platform.exts.worlds.single.ui;

import finio.ui.actions.FActionMap;
import finio.ui.app.AppContext;
import finio.ui.worlds.WorldContext;
import finio.ui.worlds.WorldSelectedListener;
import finio.ui.worlds.WorldsPanel;
import replete.ui.lay.Lay;

public class SingleWorldsPanel extends WorldsPanel {


    ////////////
    // FIELDS //
    ////////////

    private FActionMap actionMap;
    private WorldContext wc;


    /////////////////
    // CONSTRUCTOR //
    /////////////////

    public SingleWorldsPanel(final AppContext ac, final FActionMap actionMap) {
        super(ac);
        Lay.hn(this, WorldsPanel.STYLE);

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

    // Largely do nothing...

    @Override
    public void setSelectedIndex(int i) {}
    @Override
    public void addWorld(WorldContext wc, int i) {
        if(this.wc != null || wc == null || wc.getWorldPanel() == null) {
            throw new IllegalArgumentException();
        }
        Lay.BLtg(this,
            "C", wc.getWorldPanel()
        );
        updateUI();
        this.wc = wc;
    }
    @Override
    public void removeWorld(WorldContext wc) {
        if(wc == null || wc != this.wc) {
            throw new IllegalArgumentException();
        }
        remove(wc.getWorldPanel());
        updateUI();
        this.wc = null;
    }
    @Override
    public void addWorldSelectedListener(WorldSelectedListener listener) {}
    @Override
    public void removeWorldSelectedListener(WorldSelectedListener listener) {}
}
