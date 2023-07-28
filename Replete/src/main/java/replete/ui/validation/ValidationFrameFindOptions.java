package replete.ui.validation;

import java.util.function.Predicate;

public class ValidationFrameFindOptions {
    Predicate<ValidationMessage> criteria;
    public Predicate<ValidationMessage> getCriteria() {
        return criteria;
    }
    public ValidationFrameFindOptions setCriteria(Predicate<ValidationMessage> criteria) {
        this.criteria = criteria;
        return this;
    }
}
