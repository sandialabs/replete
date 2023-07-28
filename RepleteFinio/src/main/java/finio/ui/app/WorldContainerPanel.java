package finio.ui.app;

import java.awt.BorderLayout;

import finio.ui.fpanel.FPanel;
import finio.ui.world.WorldPanel;
import replete.text.StringUtil;
import replete.ui.lay.Lay;

public class WorldContainerPanel extends FPanel {


    ///////////
    // FIELD //
    ///////////

    private WorldPanel pnlWorld;
    private String worldName;


    /////////////////
    // CONSTRUCTOR //
    /////////////////

    public WorldContainerPanel(WorldPanel pnlWorld, String worldName) {
        this.pnlWorld = pnlWorld;
        this.worldName = worldName;

        Lay.BLtg(this,
            "C", pnlWorld
        );
    }


    //////////////////////////
    // ACCESSORS / MUTATORS //
    //////////////////////////

    // Accessors

    public WorldPanel getWorldPanel() {
        return pnlWorld;
    }
    public String getWorldName() {
        return worldName;
    }

    // Accessors (Computed)

    public String createTabTitle() {         // Duplicate
        return
            "<html><i>World:</i>" +
            (worldName != null ? " " + StringUtil.cleanXmlCdata(worldName) : "") + "</html>";
    }

    // Mutators

    public void setWorldPanel(WorldPanel pnlWorld) {
        removeAll();
        // remove listeners...

        this.pnlWorld = pnlWorld;
        // add listeners
        add(pnlWorld, BorderLayout.CENTER);

        updateUI();
    }
    public void setWorldName(String worldName) {
        this.worldName = worldName;
    }
}
