package replete.mail.errors;

/**
 * Should be thrown whenever the MailTemplateManager
 * cannot instantiate a subclass of MailTemplate.
 * This should rarely if ever happen.
 *
 * @author Derek Trumbo
 */

public class MailTemplateConstructException extends RuntimeException {
    public MailTemplateConstructException(String msg, Throwable cause) {
        super(msg, cause);
    }
}
