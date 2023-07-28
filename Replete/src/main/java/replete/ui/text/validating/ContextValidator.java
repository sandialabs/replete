package replete.ui.text.validating;

import replete.ui.validation.ValidationContext;

// This interface is specifically for the ValidatingTextField
// class.
// This interface is not meant to extend Validatable.  That
// is for controls only.  This interface is specific to the
// implementation of the ValidatingTextField and does help
// support that control in its obligations to that interface.
public interface ContextValidator extends Validator {

    // Standard message strings for context validators to use.
    // (so we don't have to decide know if they should have
    // periods at the end!!).  These might be better in anoter
    // location but are here for now.
    public static final String INP_RQD = "Input required";
    public static final String INV_FMT = "Invalid format";
    public static final String INV_VAL = "Invalid value";

    // Legacy method implementation (basically converts the context
    // into a single boolean value based on semantics).  In this case,
    // when accept() returns false, then the "invalid" color is
    // show on the ValidatingTextField's background.  The text field
    // itself doesn't recognize/understand Validatable/ValidationContext.
    // Thus, information and warning messages have no relevance here.
    public default boolean accept(ValidatingTextField txt, String text) {
        ValidationContext context = new ValidationContext();
        validateInput(txt, text, context);
        return !context.hasError();
    }

    boolean validateInput(ValidatingTextField txt, String text, ValidationContext context);
    // ^ Can add messages only, no children
    //   Return type means nothing at this point and only serves
    //   to enable the developer to use && short-circuiting in
    //   implementations to make them simpler.  At least allows
    //   for a linear dependence chain in really easy to read
    //   way.
    //   If the return value DID have a mean that you'd like to
    //   stick to, rather than it just providing a syntactic
    //   sweetness to your code, then it means that NO messages
    //   (neither INFO, WARN, nor ERROR) where added to the frame
    //   during the method call.  This currently is not enforced
    //   of course however.
}
