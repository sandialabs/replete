package finio.ui.world;

import finio.ui.worlds.WorldContext;

public class RenameWorldEvent {


    ////////////
    // FIELDS //
    ////////////

    private WorldContext wc;
    private int worldIndex;
    private String previousName;
    private String currentName;
    // AppContext/AppPanel someday?


    /////////////////
    // CONSTRUCTOR //
    /////////////////

    public RenameWorldEvent(WorldContext wc, int worldIndex,
                            String previousName, String currentName) {
        this.wc = wc;
        this.worldIndex = worldIndex;
        this.previousName = previousName;
        this.currentName = currentName;
    }


    ///////////////
    // ACCESSORS //
    ///////////////

    public WorldContext getWorldContext() {
        return wc;
    }
    public int getWorldIndex() {
        return worldIndex;
    }
    public String getPreviousName() {
        return previousName;
    }
    public String getCurrentName() {
        return currentName;
    }
}
