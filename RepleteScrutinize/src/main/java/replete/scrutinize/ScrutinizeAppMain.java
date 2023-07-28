package replete.scrutinize;

import replete.plugins.PluginManager;
import replete.scrutinize.core.Scrutinizer;
import replete.scrutinize.wrappers.ScrutinizationSc;
import replete.util.AppMain;

public class ScrutinizeAppMain extends AppMain {

    // Scrutinize is about inspecting as much as possible about
    // a machine with as little configuration as possible.
    // Core principle is being able to control what data is retrieved.
    // Don't want to just send over anything -- like a raw, pure
    // information reflection dive would do.  So we're trying to
    // strike a balance.
    // Other tasks:
    //  - able to do fields in addition to methods (cuz some constants are good to read)
    //  - unification of instances (identical native objects get identical Sc objects)
    //  - Plug-in diving
    // This architecture attempts to preserve information hiding
    // by only reading values from methods, and not fields, but
    // tries to not invoke any methods except accessors.  This is
    // done 1. by having a manual step where I have to list the
    // methods I want called below, and 2. some mechanics restricting
    // that you can' call void methods, for example, to help ensure
    // wrong methods are not invoked.


    //////////
    // TEST //
    //////////

    public static void main(String[] args) throws Exception {
        PluginManager.initialize(ScrutinizePlugin.class);
        Scrutinizer.initialize();
        ScrutinizationSc S = new ScrutinizationSc();
        S.load();
        S.print();
    }
}
