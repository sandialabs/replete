package finio.platform.exts.manager.orientdb;

import java.io.File;

import finio.manager.ManagedParameters;

public class OrientDbFileManagedParameters extends ManagedParameters {


    ////////////
    // FIELDS //
    ////////////

    private File file;


    //////////////////
    // CONSTRUCTORS //
    //////////////////

    public OrientDbFileManagedParameters() {
        // Nothing
    }
    public OrientDbFileManagedParameters(File file) {
        this.file = file;
    }


    //////////////////////////
    // ACCESSORS / MUTATORS //
    //////////////////////////

    // Accessors

    public File getFile() {
        return file;
    }

    // Mutators (Builder)

    public OrientDbFileManagedParameters setFile(File file) {
        this.file = file;
        return this;
    }
}
