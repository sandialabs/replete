package finio.appstate;

import java.io.File;

public class WorldBundle {


    ////////////
    // FIELDS //
    ////////////

    private String label;
    private File file;


    /////////////////
    // CONSTRUCTOR //
    /////////////////

    public WorldBundle(String label, File file) {
        this.label = label;
        this.file = file;
    }


    ///////////////
    // ACCESSORS //
    ///////////////

    public String getLabel() {
        return label;
    }
    public File getFile() {
        return file;
    }
}
