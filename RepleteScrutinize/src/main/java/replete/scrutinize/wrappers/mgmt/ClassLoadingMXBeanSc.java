package replete.scrutinize.wrappers.mgmt;

import java.lang.management.ClassLoadingMXBean;

import replete.scrutinize.core.BaseSc;

public class ClassLoadingMXBeanSc extends BaseSc {


    ////////////////
    // OVERRIDDEN //
    ////////////////

    @Override
    public Class<?> getHandledClass() {
        return ClassLoadingMXBean.class;
    }

    @Override
    public String[] getExtractedFields() {
        return new String[] {
            "getLoadedClassCount",
            "getTotalLoadedClassCount",
            "getUnloadedClassCount",
            "isVerbose"
        };
    }
}
