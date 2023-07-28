package replete.scrutinize.wrappers.mgmt;

import java.lang.management.ManagementFactory;

import replete.scrutinize.core.BaseSc;

public class ManagementFactorySc extends BaseSc {    // DONE


    ////////////////
    // OVERRIDDEN //
    ////////////////

    @Override
    public Class<?> getHandledClass() {
        return ManagementFactory.class;
    }

    @Override
    public String[] getExtractedFields() {
        return new String[] {
            "getClassLoadingMXBean",
            "getCompilationMXBean",
            "getGarbageCollectorMXBeans",
            "getMemoryManagerMXBeans",
            "getMemoryMXBean",
            "getMemoryPoolMXBeans",
            "getOperatingSystemMXBean",
            "getRuntimeMXBean",
            "getThreadMXBean",
        };
    }
}