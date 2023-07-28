package replete.scrutinize.wrappers.mgmt;

import java.lang.management.CompilationMXBean;

import replete.scrutinize.core.BaseSc;

public class CompilationMXBeanSc extends BaseSc {


    ////////////////
    // OVERRIDDEN //
    ////////////////

    @Override
    public Class<?> getHandledClass() {
        return CompilationMXBean.class;
    }

    @Override
    public String[] getExtractedFields() {
        return new String[] {
            "getName",
            "getTotalCompilationTime",
            "isCompilationTimeMonitoringSupported"
        };
    }
}
