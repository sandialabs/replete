package replete.scrutinize.wrappers.mgmt;

import java.lang.management.ThreadInfo;

import replete.scrutinize.core.BaseSc;

public class ThreadInfoSc extends BaseSc {


    ////////////////
    // OVERRIDDEN //
    ////////////////

    @Override
    public Class<?> getHandledClass() {
        return ThreadInfo.class;
    }

    @Override
    public String[] getExtractedFields() {
        return new String[] {
            "getBlockedCount",
            "getBlockedTime",
            "getLockName",
            "getLockOwnerId",
            "getLockOwnerName",
            "getLockedMonitors;count",
            "getLockedSynchronizers;count",
            "getLockInfo",
            "getStackTrace;count",
            "getThreadId",
            "getThreadName",
            "getThreadState",
            "getWaitedCount",
            "getWaitedTime",
            "isInNative",
            "isSuspended",
        };
    }
}
