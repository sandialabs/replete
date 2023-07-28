package replete.pstate.example;

import java.io.File;

import replete.pstate.StateReader;


/**
 * @author Derek Trumbo
 */

public class CfgStateReader implements StateReader {
    protected File cfgFile;
    protected AppState state;

    public CfgStateReader(File file, AppState st) {
        cfgFile = file;
        state = st;
    }

    public boolean read() {
        // {open and parse cfgFile, set the 'z' var of state}
        return true;
    }
}
