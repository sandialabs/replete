package replete.mail.errors;

/**
 * Should be thrown whenever a mail template is asked for
 * that does not exist in the mail template repository.
 * This should happen only in early development as the
 * developer decides what template they are asking for.
 *
 * @author Derek Trumbo
 */

public class MailTemplateNotFoundException extends RuntimeException {
    public MailTemplateNotFoundException(String msg) {
        super(msg);
    }
}
