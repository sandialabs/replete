package replete.ui.text.validating;

import replete.ui.validation.ValidationContext;

public class NonZeroLengthStringContextValidator implements ContextValidator {
    @Override
    public boolean validateInput(ValidatingTextField txt, String text, ValidationContext context) {
        return
            context.error(INP_RQD, null, text == null || text.isEmpty());
    }
}
