package replete.bc;

import replete.plugins.ExtensionPoint;

public interface SoftwareVersionLookup extends ExtensionPoint {

    // Provides a short version string (i.e. "X.Y.Z") for a live
    // class loaded in the JVM.
    String getVersion(Class clazz);
}
