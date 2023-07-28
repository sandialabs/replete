package finio.platform.exts.manager.xstream;

import java.io.File;

import finio.manager.ManagedParameters;

public class XStreamFileManagedParameters extends ManagedParameters {


    ////////////
    // FIELDS //
    ////////////

    private File file;


    //////////////////
    // CONSTRUCTORS //
    //////////////////

    public XStreamFileManagedParameters() {
        // Nothing
    }
    public XStreamFileManagedParameters(File file) {
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

    public XStreamFileManagedParameters setFile(File file) {
        this.file = file;
        return this;
    }
}
