package replete.ui.validation;

// Never "caused" by any other specific exception, but rather
// exists due to some arbitrary less-than-desirable state within
// the enclosed validation context.
public class ValidationContextWrapperException extends RuntimeException {
    private ValidationContext context;
    public ValidationContextWrapperException(String message, ValidationContext context) {
        super(message);
        this.context = context;
    }
    public ValidationContext getContext() {
        return context;
    }
    @Override
    public String getMessage() {
        return super.getMessage() + "\n" + context.toString();
    }
}
