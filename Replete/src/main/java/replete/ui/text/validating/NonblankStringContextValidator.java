package replete.ui.text.validating;

import replete.text.StringUtil;
import replete.ui.validation.ValidationContext;

public class NonblankStringContextValidator implements ContextValidator {
    @Override
    public boolean validateInput(ValidatingTextField txt, String text, ValidationContext context) {
        return
            context.error(INP_RQD, null, StringUtil.isBlank(text));
    }
}
