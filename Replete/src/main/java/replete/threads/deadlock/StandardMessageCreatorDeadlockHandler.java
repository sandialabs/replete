package replete.threads.deadlock;

import java.lang.management.LockInfo;
import java.lang.management.MonitorInfo;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import replete.collections.Pair;
import replete.text.RStringBuilder;
import replete.text.StringUtil;
import replete.threads.ThreadUtil;

public abstract class StandardMessageCreatorDeadlockHandler implements DeadlockHandler {

    @Override
    public void handleDeadlock(DeadlockDetector detector, Map<Long, DeadlockedThreadDescriptor> deadlockedThreadDescriptors) {
        List<DeadlockedThreadDescriptor> descs = new ArrayList<>();
        for(DeadlockedThreadDescriptor desc : deadlockedThreadDescriptors.values()) {
            if(includeDescriptor(desc)) {
                descs.add(desc);
            }
        }
        if(!descs.isEmpty()) {
            RStringBuilder buffer = new RStringBuilder();
            buffer.appendln("|--------> ! <--------|");
            buffer.appendln("** DEADLOCK DETECTED **");
            Map<Thread, StackTraceElement[]> stackTraceMap = Thread.getAllStackTraces();
            for(DeadlockedThreadDescriptor desc : descs) {
                String descMsg = createDescriptorMessage(desc, stackTraceMap);
                buffer.append(descMsg);
            }
            buffer.appendln("Average Find Duration: " + detector.getFindThreadsDurationStats().getAverage() + " ms");
            buffer.append("|--------> ! <--------|");
            processMessage(buffer.toString());
        }
    }

    protected String createDescriptorMessage(DeadlockedThreadDescriptor desc,
                                             Map<Thread, StackTraceElement[]> stackTraceMap) {
        String in = StringUtil.spaces(4);
        String in2 = StringUtil.spaces(8);
        RStringBuilder buffer = new RStringBuilder();
        buffer.appendln("--> " + StringUtil.prefixNewLines(desc.toString(), in, false));
        Pair<Thread, StackTraceElement[]> threadTrace =
            ThreadUtil.getThreadTrace(desc.getId(), stackTraceMap);
        if(threadTrace != null) {
            Thread thread = threadTrace.getValue1();
            buffer.appendln(in + "Thread Class: " + (thread == null ? "[NO THREAD!]" : thread.getClass().getName()));
            for(StackTraceElement ste : threadTrace.getValue2()) {
                buffer.appendln(in2 + ste.toString().trim());
            }
        }
        if(desc.getInfo() != null) {
            MonitorInfo[] monInfos = desc.getInfo().getLockedMonitors();
            LockInfo[] lockInfos = desc.getInfo().getLockedSynchronizers();
            String lockOwner = desc.getInfo().getLockOwnerName();
            if(monInfos.length != 0) {
                buffer.appendln(in + "Monitor Infos:");
                for(MonitorInfo monInfo : monInfos) {
                    buffer.appendln(in2 + monInfo);
                }
            }
            if(lockInfos.length != 0) {
                buffer.appendln(in + "Lock Infos:");
                for(LockInfo lockInfo : lockInfos) {
                    buffer.appendln(in2 + lockInfo);
                }
            }
            if(lockOwner != null) {
                buffer.appendln(in + "{Lock Owner: " + lockOwner + "}");
            }
        }
        return buffer.toString();
    }


    //////////////
    // ABSTRACT //
    //////////////

    protected abstract boolean includeDescriptor(DeadlockedThreadDescriptor desc);
    protected abstract void processMessage(String msg);
}
