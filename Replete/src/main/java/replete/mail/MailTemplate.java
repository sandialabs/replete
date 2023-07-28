package replete.mail;

import java.net.URL;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import replete.io.FileUtil;
import replete.mail.errors.MailTemplateNotFoundException;
import replete.mail.errors.MailTemplateReadException;


/**
 * @author Derek Trumbo
 */

public abstract class MailTemplate {

    ////////////////////////
    // INSTANCE VARIABLES //
    ////////////////////////

    // These are instance variables because they are
    // extracted from the template's text.

    private String subject;
    private String body;
    private Set<String> vars;

    /////////////////////////////
    // TEMPLATE INITIALIZATION //
    /////////////////////////////

    // The purpose of this constructor is to populate this
    // MailTemplate object's subject, body, and vars instance
    // variables.  It does this by asking the subclass
    // for its template name, and then grabbing the appropriate
    // text and parsing it.
    protected MailTemplate() {
        extractAndPopulateTemplate();
    }

    // For testing purposes only!  Do not call this
    // constructor except for testing purposes.
    private MailTemplate(String text) {
        parse(text);
    }

    private void extractAndPopulateTemplate() {
        String name = getTemplateName();     // Ask subclass for name.
        String tpl = getTemplateText(name);  // Get text.
        parse(tpl);                          // Populate instance variables.
    }

    // Given a name of a template, extract the text from
    // the mail template repository.  Right now this is the
    // source tree.
    private String getTemplateText(String name) throws MailTemplateNotFoundException, MailTemplateReadException {
        URL textURL = MailTemplate.class.getResource("tpl/" + name);
        if(textURL == null) {
            throw new MailTemplateNotFoundException("could not find mail template named '" + name + "'");
        }
        try {
            return FileUtil.getTextContent(textURL.openStream()).trim();
        } catch(Exception e) {
            throw new MailTemplateReadException("could not read mail template named '" + name + "'", e);
        }
    }

    /**
     * Given an email's text, parse out the subject, body, and
     * variable from it and populate this template object.
     */
    private void parse(String text) {
        final String SUBJ_PREFIX = "Subject:";

        if(text == null) {
            throw new IllegalArgumentException("'text' cannot be null");
        }

        text = text.trim();

        if(text.toLowerCase().startsWith(SUBJ_PREFIX.toLowerCase())) {
            int lineOneEnd = text.indexOf("\n");
            if(lineOneEnd == -1) {
                subject = text.substring(SUBJ_PREFIX.length()).trim();
                body = "";
            } else {
                subject = text.substring(SUBJ_PREFIX.length(), lineOneEnd).trim();
                body = text.substring(lineOneEnd).trim();
            }
        } else {
            subject = "";
            body = text;
        }

        Set<String> subjectVars = MailVariableResolver.findVariables(subject);
        Set<String> bodyVars = MailVariableResolver.findVariables(body);

        Set<String> allVars = new HashSet<String>();
        allVars.addAll(subjectVars);
        allVars.addAll(bodyVars);

        vars = Collections.unmodifiableSet(allVars);
    }

    ///////////////
    // ACCESSORS //
    ///////////////

    public String getSubject() {
        return subject;
    }

    public String getBody() {
        return body;
    }

    public Set<String> getVars() {
        return vars;
    }

    // An interesting toString for debugging purposes.
    @Override
    public String toString() {
        return "[Format: \"" +
            getContentType() + "\"][Name: \"" +
            getTemplateName() + "\"][Subject: \"" +
            subject + "\"][Body: \"" +
            body + "\"][Vars: " +
            vars + "]";
    }

    ////////////////////////////////////
    // TO-BE OVERRIDDEN BY SUBCLASSES //
    ////////////////////////////////////

    // Base class mail template assumes plain text.  Subclasses
    // should override this method to provide a different
    // content type (e.g. "text/html").
    public String getContentType() {
        return "text/plain";
    }

    // Subclasses must provide a string name that represents
    // their template.  This will be used as the key to find
    // the template in wherever the templates' text is stored.
    public abstract String getTemplateName();

    //////////
    // TEST //
    //////////

    public static void main(String[] args) {
        String text0 = "";
        System.out.println(makeTemplate(text0));

        String text1 = "The moon is a harsh mistress.";
        System.out.println(makeTemplate(text1));

        String text2 = "    \n\n  The moon is a harsh ${noun}.  \n \n ";
        System.out.println(makeTemplate(text2));

        String text3 = "subject: Moon Note";
        System.out.println(makeTemplate(text3));

        String text4 = "\n\n SUBJECT:   Moon Note: ${addlText} \n\n ";
        System.out.println(makeTemplate(text4));

        String text5 = "\n\n\n   subject:    What question ${verb} you most?    \n    The ${adj} paradox thrives. \n\n And you know it. ";
        System.out.println(makeTemplate(text5));
    }

    private static MailTemplate makeTemplate(String text) {
        return new MailTemplate(text) {
            @Override
            public String getTemplateName() {
                return "dummy";
            }
            @Override
            public String getContentType() {
                return "text/html";
            }
        };
    }
}
