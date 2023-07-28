package replete.scrutinize.wrappers.sys;

import java.awt.Robot;

import replete.scrutinize.core.BaseSc;


public class RobotSc extends BaseSc {    // DONE


    ////////////////
    // OVERRIDDEN //
    ////////////////

    @Override
    public Class<?> getHandledClass() {
        return Robot.class;
    }

    @Override
    public String[] getExtractedFields() {
        return new String[] {
            "autoDelay",
            "autoWaitForIdle"
        };
    }
}
