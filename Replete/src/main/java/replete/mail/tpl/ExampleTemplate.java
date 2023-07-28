package replete.mail.tpl;

import replete.mail.MailTemplate;

/**
 * @author Derek Trumbo
 */

public class ExampleTemplate extends MailTemplate {
    public static final String SIG = "sig";
    public static final String SUBJECT = "subject";
    public static final String NAME = "name";

    @Override
    public String getTemplateName() {
        return "exampleTemplate";
    }

    @Override
    public String getContentType() {
        return "text/html";
    }
}
