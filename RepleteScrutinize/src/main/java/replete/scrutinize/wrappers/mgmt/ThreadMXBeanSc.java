package replete.scrutinize.wrappers.mgmt;

import java.lang.management.ThreadInfo;
import java.lang.management.ThreadMXBean;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import replete.scrutinize.core.BaseSc;

public class ThreadMXBeanSc extends BaseSc {


    ////////////////
    // OVERRIDDEN //
    ////////////////

    @Override
    public Class<?> getHandledClass() {
        return ThreadMXBean.class;
    }

    @Override
    public String[] getExtractedFields() {
        return new String[] {
            "getCurrentThreadCpuTime",
            "getCurrentThreadUserTime",
            "getDaemonThreadCount",
            "getPeakThreadCount",
            "getThreadCount",
            "getTotalStartedThreadCount",
            "getAllThreadIds",
            "isCurrentThreadCpuTimeSupported",
            "isObjectMonitorUsageSupported",
            "isSynchronizerUsageSupported",
            "isThreadContentionMonitoringEnabled",
            "isThreadContentionMonitoringSupported",
            "isThreadCpuTimeEnabled",
            "isThreadCpuTimeSupported",
            "getObjectName", //????
        };
    }

    @Override
    public Map<String, Object> getCustomFields(Object nativeObj) {
        ThreadMXBean bean = (ThreadMXBean) nativeObj;
        Map<String, Object> fields = new TreeMap<>();

        fields.put("Cur Thread CPU Time",  bean.getThreadCpuTime(Thread.currentThread().getId()));
        fields.put("Cur Thread User Time", bean.getThreadUserTime(Thread.currentThread().getId()));
        fields.put("Cur Thread Info",      bean.getThreadInfo(Thread.currentThread().getId()));

        List<ThreadInfo> tis = new ArrayList<>();
        for(Long l : bean.getAllThreadIds()) {
            ThreadInfo ti = bean.getThreadInfo(l);
            tis.add(ti);
        }

        fields.put("Thread Infos", tis);

        return fields;
    }
}
