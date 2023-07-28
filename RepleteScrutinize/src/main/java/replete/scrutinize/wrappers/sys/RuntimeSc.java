package replete.scrutinize.wrappers.sys;

import replete.scrutinize.core.BaseSc;

public class RuntimeSc extends BaseSc {    // DONE


    ////////////////
    // OVERRIDDEN //
    ////////////////

    @Override
    public Class<?> getHandledClass() {
        return Runtime.class;
    }

    @Override
    public String[] getExtractedFields() {
        return new String[] {
            "availableProcessors",
            "freeMemory",
            "getRuntime",
            "maxMemory",
            "totalMemory"
        };
    }
}
