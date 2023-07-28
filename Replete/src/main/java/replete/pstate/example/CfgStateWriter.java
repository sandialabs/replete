package replete.pstate.example;

import java.io.File;

import replete.pstate.StateWriter;


/**
 * @author Derek Trumbo
 */

public class CfgStateWriter implements StateWriter {
    protected File cfgFile;
    protected AppState state;

    public CfgStateWriter(File file, AppState st) {
        cfgFile = file;
        state = st;
    }

    public boolean write() {
        // {write the 'z' variable of state to the file in standard format}
        return true;
    }
}
