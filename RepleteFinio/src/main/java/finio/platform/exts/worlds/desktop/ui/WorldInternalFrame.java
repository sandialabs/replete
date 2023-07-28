package finio.platform.exts.worlds.desktop.ui;

import javax.swing.JInternalFrame;

import finio.ui.worlds.WorldContext;
import replete.ui.images.concepts.CommonConcepts;
import replete.ui.images.concepts.ImageLib;
import replete.ui.lay.Lay;

public class WorldInternalFrame extends JInternalFrame {


    /////////////////
    // CONSTRUCTOR //
    /////////////////

    public WorldInternalFrame(WorldContext wc, int i) {
        super(wc.getTitle(), true, true, true, true);

        Lay.BLtg(this,
            "C", wc.getWorldPanel()
//            "size=400,location=" + (40 * i)  TODO someday fix this...
        );

        setLocation(40 * i, 40 * i);
        setSize(400, 400);
        setFrameIcon(ImageLib.get(CommonConcepts.WORLD));
    }
}
