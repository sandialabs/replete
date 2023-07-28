package finio.platform.exts.manager.orientdb.nt;

import finio.manager.FixedFMapWrapperManagedNonTerminal;
import finio.platform.exts.manager.orientdb.OrientDbFileManagedParameters;
import finio.platform.exts.manager.orientdb.OrientDbFileMapManager;

public class OdbRootNt extends FixedFMapWrapperManagedNonTerminal {


    ///////////
    // FIELD //
    ///////////

    private OrientDbFileManagedParameters params;


    /////////////////
    // CONSTRUCTOR //
    /////////////////

    public OdbRootNt(OrientDbFileMapManager manager) {
        super(manager);
    }

    @Override
    protected void initSimple() {
        super.initSimple();

        put("User Classes", new OdbUserClassesNt(
            (OrientDbFileMapManager) getManager()));
    }
}
