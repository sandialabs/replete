package replete.scrutinize.wrappers.mgmt;

import java.lang.management.MemoryManagerMXBean;

import replete.scrutinize.core.BaseSc;

public class MemoryManagerMXBeanSc extends BaseSc {


    ////////////////
    // OVERRIDDEN //
    ////////////////

    @Override
    public Class<?> getHandledClass() {
        return MemoryManagerMXBean.class;
    }

    @Override
    public String[] getExtractedFields() {
        return new String[] {
            "getName",
            "getMemoryPoolNames",
            "isValid"
        };
    }
}
