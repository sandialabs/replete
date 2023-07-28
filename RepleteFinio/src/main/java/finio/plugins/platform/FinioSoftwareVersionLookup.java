package finio.plugins.platform;

import finio.SoftwareVersion;
import replete.bc.ClassCompareSoftwareVersionLookup;

public class FinioSoftwareVersionLookup extends ClassCompareSoftwareVersionLookup {

    // The SoftwareVersion class is by convention always in a versioned project's
    // top-level source package and thus it serves as a good class to compare
    // other classes' packages to.
    @Override
    protected Class getCompareClass() {
        return SoftwareVersion.class;
    }

    @Override
    protected String getSoftwareVersion() {
        return SoftwareVersion.get().getVersion();
    }

}
