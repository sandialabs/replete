package replete.scrutinize.wrappers.mgmt;

import java.lang.management.LockInfo;

import replete.scrutinize.core.BaseSc;

public class LockInfoSc extends BaseSc {


    ////////////////
    // OVERRIDDEN //
    ////////////////

    @Override
    public Class<?> getHandledClass() {
        return LockInfo.class;
    }

    @Override
    public String[] getExtractedFields() {
        return new String[] {
            "className",
            "identityHashCode",
        };
    }
}
