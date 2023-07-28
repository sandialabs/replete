package replete.cli.validator;

import replete.cli.options.Option;

public interface OptionValueValidator<T> {
    String validate(Option<T> option, T value);
}
