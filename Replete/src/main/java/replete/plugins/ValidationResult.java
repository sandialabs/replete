package replete.plugins;

import java.io.Serializable;

public class ValidationResult implements Serializable {
    private boolean valid;
    private String errorMsg;
    private Throwable error;

    public ValidationResult() {
        valid = true;
    }
    public ValidationResult(boolean valid, String errorMsg, Throwable error) {
        this.valid = valid;
        this.errorMsg = errorMsg;
        this.error = error;
    }

    public boolean isValid() {
        return valid;
    }
    public String getErrorMsg() {
        return errorMsg;
    }
    public Throwable getError() {
        return error;
    }
}