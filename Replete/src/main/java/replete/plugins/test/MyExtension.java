package replete.plugins.test;

import replete.plugins.ValidationResult;

public class MyExtension implements MyExtensionPoint {
    public ValidationResult validate() {
        return new ValidationResult(false, "Could not find file.", null);
    }
}
