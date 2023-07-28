package replete.scrutinize.wrappers.mgmt;

import java.lang.management.GarbageCollectorMXBean;

import replete.scrutinize.core.BaseSc;

public class GarbageCollectorMXBeanSc extends BaseSc {


    ////////////////
    // OVERRIDDEN //
    ////////////////

    @Override
    public Class<?> getHandledClass() {
        return GarbageCollectorMXBean.class;
    }

    @Override
    public String[] getExtractedFields() {
        return new String[] {
            "getCollectionCount",
            "getCollectionTime",
            "getName",               //???????
            "getMemoryPoolNames",    //???????
            "isValid"                //???????
        };
    }
}
