package replete.scrutinize.wrappers.mgmt;

import java.lang.management.RuntimeMXBean;

import replete.scrutinize.core.BaseSc;

public class RuntimeMXBeanSc extends BaseSc {


    ////////////////
    // OVERRIDDEN //
    ////////////////

    @Override
    public Class<?> getHandledClass() {
        return RuntimeMXBean.class;
    }

    @Override
    public String[] getExtractedFields() {
        return new String[] {
            "getBootClassPath",
            "getClassPath",
            "getLibraryPath",
            "getManagementSpecVersion",
            "getName",
            "getSpecName",
            "getSpecVendor",
            "getSpecVersion",
            "getStartTime",
            "getUptime",
            "getVmName",
            "getVmVendor",
            "getVmVersion",
            "getInputArguments",
            "getSystemProperties",
            "isBootClassPathSupported",
        };
    }
}
