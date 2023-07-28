package replete.scrutinize.wrappers.ui;

import replete.scrutinize.core.BaseSc;

public class SecurityManagerSc extends BaseSc {    // DONE


    ////////////////
    // OVERRIDDEN //
    ////////////////

    @Override
    public Class<?> getHandledClass() {
        return SecurityManager.class;
    }

    @Override
    public String[] getExtractedFields() {
        return new String[] {
            "packageAccessValid;field",        // Actually private fields.
            "packageAccess;field",             // Will only be set if certain methods are
            "packageDefinitionValid;field",    // called within the base class.
            "packageDefinition;field",
        };
    }
}
