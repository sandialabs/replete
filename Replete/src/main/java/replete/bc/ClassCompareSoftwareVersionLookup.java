package replete.bc;

import replete.text.StringUtil;

// Utility base class to provide lookup classes with some
// standardized logic.  Given a top-level "anchor" class,
// this lookup expresses the version of the software for
// a given class if that class is in a package under that
// of the anchor class.

public abstract class ClassCompareSoftwareVersionLookup implements SoftwareVersionLookup {


    ////////////////
    // OVERRIDDEN //
    ////////////////

    @Override
    public String getVersion(Class clazz) {
        Class compareClass = getCompareClass();           // Usually a top-level class in the client project
        String pkgName = clazz.getPackage().getName();
        String topName = compareClass.getPackage().getName();
        if(StringUtil.startsWithHier(pkgName, topName, '.')) {
            return getSoftwareVersion();
        }
        return null;
    }


    //////////////
    // ABSTRACT //
    //////////////

    protected abstract Class getCompareClass();
    protected abstract String getSoftwareVersion();
}
