package replete.scrutinize.wrappers.mgmt;

import java.lang.management.OperatingSystemMXBean;
import java.util.Map;
import java.util.TreeMap;

import com.sun.management.UnixOperatingSystemMXBean;

import replete.scrutinize.core.BaseSc;

public class OperatingSystemMXBeanSc extends BaseSc {


    ////////////////
    // OVERRIDDEN //
    ////////////////

    @Override
    public Class<?> getHandledClass() {
        return OperatingSystemMXBean.class;
    }

    @Override
    public String[] getExtractedFields() {
        return new String[] {
            "getArch",
            "getAvailableProcessors",
            "getName",
            "getSystemLoadAverage",
            "getVersion",
        };
    }

    @Override
    public Map<String, Object> getCustomFields(Object nativeObj) {
        OperatingSystemMXBean i = (OperatingSystemMXBean) nativeObj;
        Map<String, Object> fields = new TreeMap<>();
        if(i instanceof UnixOperatingSystemMXBean) {
            UnixOperatingSystemMXBean uos = (UnixOperatingSystemMXBean) i;
            fields.put("getCommittedVirtualMemorySize", uos.getCommittedVirtualMemorySize());
            fields.put("getFreePhysicalMemorySize",     uos.getFreePhysicalMemorySize());
            fields.put("getFreeSwapSpaceSize",          uos.getFreeSwapSpaceSize());
            fields.put("getMaxFileDescriptorCount",     uos.getMaxFileDescriptorCount());
            fields.put("getOpenFileDescriptorCount",    uos.getOpenFileDescriptorCount());
            fields.put("getProcessCpuTime",             uos.getProcessCpuTime());
            fields.put("getTotalPhysicalMemorySize",    uos.getTotalPhysicalMemorySize());
            fields.put("getTotalSwapSpaceSize",         uos.getTotalSwapSpaceSize());
            fields.put("UsedPhysicalMemorySize",        uos.getTotalPhysicalMemorySize() - uos.getFreePhysicalMemorySize());
        }
        return fields;
    }
}
