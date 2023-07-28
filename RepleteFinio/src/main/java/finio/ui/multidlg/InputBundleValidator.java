package finio.ui.multidlg;


public interface InputBundleValidator {
    public InputBundleValidationProblem[] validate(InputBundle[] bundles);
}
