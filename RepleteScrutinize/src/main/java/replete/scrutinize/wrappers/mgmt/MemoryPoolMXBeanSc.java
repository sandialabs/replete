package replete.scrutinize.wrappers.mgmt;

import java.lang.management.MemoryPoolMXBean;
import java.util.Map;
import java.util.TreeMap;

import replete.scrutinize.core.BaseSc;

public class MemoryPoolMXBeanSc extends BaseSc {


    ////////////////
    // OVERRIDDEN //
    ////////////////

    @Override
    public Class<?> getHandledClass() {
        return MemoryPoolMXBean.class;
    }

    @Override
    public String[] getExtractedFields() {
        return new String[] {
            "getName",
            "getCollectionUsage",
            "getMemoryManagerNames",
            "getPeakUsage",
            "getType",
            "getUsage",
            "isCollectionUsageThresholdSupported",
            "isUsageThresholdSupported",
            "isValid",
        };
    }

    @Override
    public Map<String, Object> getCustomFields(Object nativeObj) {
        MemoryPoolMXBean i = (MemoryPoolMXBean) nativeObj;
        Map<String, Object> fields = new TreeMap<>();
        if(i.isCollectionUsageThresholdSupported()) {
            fields.put("getCollectionUsageThreshold", i.getCollectionUsageThreshold());
            fields.put("getCollectionUsageThresholdCount", i.getCollectionUsageThresholdCount());
            fields.put("isCollectionUsageThresholdExceeded", i.isCollectionUsageThresholdExceeded());
        }
        if(i.isUsageThresholdSupported()) {
            fields.put("getUsageThreshold", i.getUsageThreshold());
            fields.put("getUsageThresholdCount", i.getUsageThresholdCount());
            fields.put("isUsageThresholdExceeded", i.isUsageThresholdExceeded());
        }
        return fields;
    }
}
