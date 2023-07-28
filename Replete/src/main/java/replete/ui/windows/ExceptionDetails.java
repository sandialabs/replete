package replete.ui.windows;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class ExceptionDetails {


    ////////////
    // FIELDS //
    ////////////

    private Object message;
    private String title;
    private String detailsMessage;
    private Throwable error;
    private boolean initiallyOpen;
    private boolean printStackTrace;
    private List<File> sourceDirs;
    private boolean forceLargeDialog;   // Just a temporary fix until we decide how to handle the different options for error dialog display


    //////////////////////////
    // ACCESSORS / MUTATORS //
    //////////////////////////

    // Accessors

    public Object getMessage() {
        return message;
    }
    public String getTitle() {
        return title;
    }
    public String getDetailsMessage() {
        return detailsMessage;
    }
    public Throwable getError() {
        return error;
    }
    public boolean isInitiallyOpen() {
        return initiallyOpen;
    }
    public boolean isPrintStackTrace() {
        return printStackTrace;
    }
    public List<File> getSourceDirs() {
        return sourceDirs;
    }
    public boolean isForceLargeDialog() {
        return forceLargeDialog;
    }

    // Mutators

    public ExceptionDetails setMessage(Object message) {
        this.message = message;
        return this;
    }
    public ExceptionDetails setTitle(String title) {
        this.title = title;
        return this;
    }
    public ExceptionDetails setDetailsMessage(String detailsMessage) {
        this.detailsMessage = detailsMessage;
        return this;
    }
    public ExceptionDetails setError(Throwable error) {
        this.error = error;
        return this;
    }
    public ExceptionDetails setInitiallyOpen(boolean initiallyOpen) {
        this.initiallyOpen = initiallyOpen;
        return this;
    }
    public ExceptionDetails setPrintStackTrace(boolean printStackTrace) {
        this.printStackTrace = printStackTrace;
        return this;
    }
    public ExceptionDetails addSourceDir(File sourceDir) {
        if(sourceDirs == null) {
            sourceDirs = new ArrayList<>();
        }
        sourceDirs.add(sourceDir);
        return this;
    }
    public ExceptionDetails setForceLargeDialog(boolean forceLargeDialog) {
        this.forceLargeDialog = forceLargeDialog;
        return this;
    }
}
