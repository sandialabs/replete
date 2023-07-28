package replete.scrutinize.wrappers.mgmt;

import java.lang.management.MemoryMXBean;

import replete.scrutinize.core.BaseSc;

public class MemoryMXBeanSc extends BaseSc {


    ////////////////
    // OVERRIDDEN //
    ////////////////

    @Override
    public Class<?> getHandledClass() {
        return MemoryMXBean.class;
    }

    @Override
    public String[] getExtractedFields() {
        return new String[] {
            "getObjectPendingFinalizationCount",
            "getHeapMemoryUsage",
            "getNonHeapMemoryUsage",
            "isVerbose",
            "getObjectName",     //???????
        };
    }
}
