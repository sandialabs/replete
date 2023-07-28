package replete.pstate.example;

import java.io.File;

import replete.pstate.StateReader;


/**
 * State readers and state writers as public classes
 * (they have the application state passed to them).
 *
 * @author Derek Trumbo
 */

public class IniStateReader implements StateReader {
    protected File iniFile;
    protected AppState state;

    public IniStateReader(File file, AppState st) {
        iniFile = file;
        state = st;
    }

    public boolean read() {
        // {open and parse iniFile, set the 'y' var of state}
        return true;
    }
}
