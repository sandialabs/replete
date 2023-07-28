package replete.scrutinize.wrappers.sys;

import replete.scrutinize.core.BaseSc;

public class SystemSc extends BaseSc {    // DONE


    ////////////////
    // OVERRIDDEN //
    ////////////////

    @Override
    public Class<?> getHandledClass() {
        return System.class;
    }

    @Override
    public String[] getExtractedFields() {
        return new String[] {
            "console:Console",
            "currentTimeMillis",
            "getenv:Environment;sort",
            "lineSeparator",
            "nanoTime",
            "properties:Properties;sort",
            "securityManager",
            "inheritedChannel"
        };
    }
}
