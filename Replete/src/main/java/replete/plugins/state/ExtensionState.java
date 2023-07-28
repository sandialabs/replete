package replete.plugins.state;

import java.io.Serializable;

import replete.plugins.ValidationResult;


public class ExtensionState implements Serializable {
    private String id;
    private ValidationResult validationResult;
    private ExtensionPointState extPointState;

    public ExtensionState(String id, ValidationResult validationResult, ExtensionPointState extPointState) {
        this.id = id;
        this.validationResult = validationResult;
        this.extPointState = extPointState;
    }

    public String getId() {
        return id;
    }
    public ValidationResult getValidationResult() {
        return validationResult;
    }
    public ExtensionPointState getExtPointState() {
        return extPointState;
    }
}
