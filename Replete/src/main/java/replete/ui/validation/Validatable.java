package replete.ui.validation;

// TODO: Consider renaming this to "ValidatableComponent"
// in reference to "JComponent".  The only reason I haven't
// done so yet, is that currently the "replete.ui.validation"
// code ins't even specific to user interfaces!  Thus, you
// could even use this on a hierarchy of general system
// components.  It's probably a crazy idea.  Avondale, for
// example already has a "rule" system to handle that kind
// of need, though it makes me of course wonder if there's
// some sort of underlying, unifying concept I've missed.
// I do WANT to make this class UI-specific due to one
// additional desired feature.  I want users to be able to
// click "Show Me" in validation visualization dialogs and
// have the validatable components (JPanels, ValidatingTextFields,
// etc.) to be highlighted/shown in the UI.  This requires
// mapping components to the frame hierarchy.  Then the
// "Show Me" button would be provide to the host UI what the
// user clicked, and using knowledge of the UI and the
// validation hiearchy, it could switch tabs, change combo
// selections, etc. to show the offending component, and
// highlight it and focus it (so user can immediately change
// the input).  I haven't put much more thought into it than that.
public interface Validatable {
    void validateInput(ValidationContext context);
    // ^Do not call super.validateInput(context) in implementation.

    // This method is a convenience method if you have a direct
    // reference to a top-level panel you'd like to have validated.
    // No need to override this method. Default methods are cool!
    default ValidationContext validateInput() {
        ValidationContext context = new ValidationContext();
        validateInput(context);
        return context;
    }
}
