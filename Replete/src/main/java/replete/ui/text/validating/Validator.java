package replete.ui.text.validating;

// This validator is specifically for the
// ValidatingTextField class.  Could name
// this class TextValidator perhaps.
public interface Validator {
    public boolean accept(ValidatingTextField txt, String text);
}
