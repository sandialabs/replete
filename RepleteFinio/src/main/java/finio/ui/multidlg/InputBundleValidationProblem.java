package finio.ui.multidlg;


public class InputBundleValidationProblem {
    private InputBundle dataBundle;
    private Exception error;

    public InputBundleValidationProblem(InputBundle file, Exception error) {
        dataBundle = file;
        this.error = error;
    }

    public InputBundle getDataBundle() {
        return dataBundle;
    }
    public Exception getError() {
        return error;
    }
}
