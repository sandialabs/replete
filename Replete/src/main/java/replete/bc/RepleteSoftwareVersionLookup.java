package replete.bc;

import replete.SoftwareVersion;
import replete.text.StringUtil;

// Does not extend ClassCompareSoftwareVersionLookup due
// to the presence of additional logic.

public class RepleteSoftwareVersionLookup implements SoftwareVersionLookup {

    // The SoftwareVersion class is by convention always in a versioned project's
    // top-level source package and thus it serves as a good class to compare
    // other classes' packages to.
    private final Class topLevelAnchorClass = SoftwareVersion.class;

    @Override
    public String getVersion(Class clazz) {
        String pkgName = clazz.getPackage().getName();
        String topName = topLevelAnchorClass.getPackage().getName();
        if(StringUtil.startsWithHier(pkgName, topName, '.')) {

            // Since the "replete" namespace is not super specific or unique,
            // check for other projects that use it but that are not managed
            // under this project's SoftwareVersion class.  This is overkill
            // but demonstrates how the framework works.
            if(StringUtil.startsWithHier(pkgName, "replete.bash", '.')            ||
                    StringUtil.startsWithHier(pkgName, "replete.extensions", '.') ||
                    StringUtil.startsWithHier(pkgName, "replete.jgraph", '.')     ||
                    StringUtil.startsWithHier(pkgName, "replete.pipeline", '.')   ||
                    StringUtil.startsWithHier(pkgName, "replete.scripting", '.')  ||
                    StringUtil.startsWithHier(pkgName, "replete.scrutinize", '.')) {
                return null;
            }

            return SoftwareVersion.get().getVersion();
        }
        return null;
    }
}
