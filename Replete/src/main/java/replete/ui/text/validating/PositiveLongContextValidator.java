package replete.ui.text.validating;

import replete.numbers.NumUtil;
import replete.ui.validation.ValidationContext;

public class PositiveLongContextValidator implements ContextValidator {
    @Override
    public boolean validateInput(ValidatingTextField txt, String text, ValidationContext context) {
        text = text.trim().replaceAll(",", "").replaceAll("_", "");
        return
            context.error(INP_RQD, null, text.isEmpty())        &&
            context.error(INV_FMT, text, !NumUtil.isLong(text)) &&
            context.error(INV_VAL, text, NumUtil.l(text) <= 0);
    }
}
