package replete.mail.errors;

/**
 * Should be thrown whenever a mail template cannot
 * be properly read.  This should rarely happen if
 * ever.
 *
 * @author Derek Trumbo
 */

public class MailTemplateReadException extends RuntimeException {
    public MailTemplateReadException(String msg, Throwable cause) {
        super(msg, cause);
    }
}
