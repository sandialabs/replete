package replete.mail;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.mail.Address;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

/**
 * @author Derek Trumbo
 */

public class Mailer {


    ////////////
    // FIELDS //
    ////////////

    // Constants

    // Common formats.
    public static final String FORMAT_PLAIN = "text/plain";
    public static final String FORMAT_HTML = "text/html";

    public static final String DEFAULT_SMTP_HOST = "mail.sandia.gov";  // Standard host

    // Regular

    private static String defaultFormat = FORMAT_PLAIN;
    private static String smtpHost = DEFAULT_SMTP_HOST;
    private static boolean disableAllSending = false;


    ///////////////
    // TEST MODE //
    ///////////////

    // The following fields and accessors/mutators are
    // to implement the sending of emails to a testing
    // team in Dev and Qual instead of the actual
    // intended recipients.
    private static boolean testMode = false;
    private static List<String> overrideTo = null;
    private static List<String> overrideCC = null;

    public static boolean isTestMode() {
        return testMode;
    }
    public static void setTestMode(boolean tm) {
        testMode = tm;
    }
    public static List<String> getOverrideTo() {
        return overrideTo;
    }
    public static void setOverrideTo(List<String> to) {
       overrideTo = to;
    }
    public static List<String> getOverrideCC() {
        return overrideCC;
    }
    public static void setOverrideCC(List<String> cc) {
        overrideCC = cc;
    }
    public static boolean isDisableAllSending() {
        return disableAllSending;
    }
    public static void setDisableAllSending(boolean das) {
        disableAllSending = das;
    }


    //////////////////////////
    // ACCESSORS / MUTATORS //
    //////////////////////////

    // Allows for a default format to be changed.
    public static String getDefaultFormat() {
        return defaultFormat;
    }
    public static void setDefaultFormat(String format) {
        defaultFormat = format;
    }

    // Allows for SMTP host to be changed.
    public static String getSmtpHost() {
        return smtpHost;
    }
    public static void setSmtpHost(String host) {
        smtpHost = host;
    }


    ////////////////////
    // PRIMARY METHOD //
    ////////////////////

    /**
     * All emails in the system are sent by this method.
     *
     * Sends an email through the corporate SMTP server.  Must specify
     * from and to addresses.  Clients must handle any exceptions thrown
     * by this method.
     *
     * Depending on how the design evolves, this method may not force
     * clients to handle exceptions, but rather just log them itself
     * and return without error.
     *
     * Since this is the core method that will send all emails in the
     * system, one day it can be augmented with code to switch over
     * to tester email addresses.
     */
    public static void sendEmail(String from, List<String> to, List<String> cc,
                                 List<String> bcc,
                                 String subject, String body, String format)
                                 throws AddressException, MessagingException {

        // Must have at least a from and to address to send an email.
        if(from == null) {
            throw new IllegalArgumentException("'from' cannot be null");
        }
        if(to == null || to.size() == 0) {
            throw new IllegalArgumentException("'to' cannot be null");
        }

        if(format == null) {
            format = FORMAT_PLAIN;   // Default to plain if not format defined.
        }

        // If in test mode, replace the to and cc addresses with
        // the override values.
        if(testMode) {
            if(overrideTo == null) {
                throw new IllegalArgumentException("'overrideTo' cannot be null");
            }

            String env = "Dev";     // TODO: Resolve this to proper environment (Dev or Qual).
            String inProdMsg1 = "*** TEST EMAIL FROM " + env.toUpperCase() + " ***";

            String inProdMsg2 = "*** In production this email would go to: " + to;
            if(cc != null) {
                inProdMsg2 += ", cc: " + cc;
            }
            inProdMsg2 += " ***";

            // Replace addresses for rest of method.
            to = overrideTo;
            cc = overrideCC;

            // Choose how to separate the lines.
            String breakType;
            if(format.equals(FORMAT_HTML)) {
                breakType = "<BR>";
            } else {
                breakType = "\n";
            }

            // Augment the body with a useful message.
            body = inProdMsg1 + breakType + inProdMsg2 + breakType + breakType + body;

            // Prefix the subject with a message.
            subject = "[Test Mode " + env + "] " + subject;
        }

        // Create the session.
        Properties props = createMailProperties();
        Session session = Session.getDefaultInstance(props, null);

        // Set from and to addresses.
        Message msg = new MimeMessage(session);
        msg.setFrom(new InternetAddress(from));

        Address[] addrs = new Address[to.size()];
        int a = 0;
        for(String t : to) {
            InternetAddress[] ia = InternetAddress.parse(t, false);
            addrs[a++] = ia[0];
        }
        msg.setRecipients(Message.RecipientType.TO, addrs);

        // Set cc address.
        if(cc != null && cc.size() != 0) {
            addrs = new Address[cc.size()];
            a = 0;
            for(String c : cc) {
                InternetAddress[] ia = InternetAddress.parse(c, false);
                addrs[a++] = ia[0];
            }
            msg.setRecipients(Message.RecipientType.CC, addrs);
        }

        // Set bcc address.
        if(bcc != null && bcc.size() != 0) {
            addrs = new Address[bcc.size()];
            a = 0;
            for(String b : bcc) {
                InternetAddress[] ia = InternetAddress.parse(b, false);
                addrs[a++] = ia[0];
            }
            msg.setRecipients(Message.RecipientType.BCC, addrs);
        }

        // Set subject and body.
        if(subject != null) {
            msg.setSubject(subject);
        }
        if(body != null) {
            msg.setContent(body, format);
        } else {
            msg.setContent("", format);   // There must be some body to satisfy JavaMail.
        }

        // Set sent date and send.
        msg.setSentDate(new Date());

        if(!disableAllSending) {
            Transport.send(msg);
        }
    }

    private static Properties createMailProperties() {
        Properties props = new Properties();
        props.setProperty("mail.smtp.host", smtpHost);
        return props;
    }


    //////////////////////////
    // SUPPLEMENTAL METHODS //
    //////////////////////////

    /**
     * A wrapper method for the 'sendEmail' method that provides
     * the current default format for the email.
     */
    public static void sendEmail(String from, List<String> to, List<String> cc,
                                 List<String> bcc, String subject, String body)
                                 throws AddressException, MessagingException {

        sendEmail(from, to, cc, bcc, subject, body, defaultFormat);

    }

    /**
     * A wrapper method for the 'sendEmail' method that will perform
     * the variable replacement itself, given a map of variable-value
     * pairs.  The class's current default format is used for the
     * email.
     */
    public static void sendEmail(String from, List<String> to, List<String> cc,
                                 List<String> bcc,
                String subject, String body, Map<String, String> vars)
                throws AddressException, MessagingException {

        subject = MailVariableResolver.resolve(subject, vars);
        body = MailVariableResolver.resolve(body, vars);

        sendEmail(from, to, cc, bcc, subject, body, defaultFormat);
    }

    /**
     * A wrapper method for the 'sendEmail' method that will perform
     * the variable replacement itself, given a map of variable-value
     * pairs.
     */
    public static void sendEmail(String from, List<String> to, List<String> cc, List<String> bcc,
                String subject, String body, String format, Map<String, String> vars)
                throws AddressException, MessagingException {

        subject = MailVariableResolver.resolve(subject, vars);
        body = MailVariableResolver.resolve(body, vars);

        sendEmail(from, to, cc, bcc, subject, body, format);
    }

    /**
     * A wrapper method for the 'sendEmail' method that extracts
     * the email subject, body, and format from a 'MailTemplate'
     * object.
     */
    public static void sendEmail(String from, List<String> to, List<String> cc,
                                 List<String> bcc, MailTemplate tpl)
                                 throws AddressException, MessagingException {

        sendEmail(from, to, cc, bcc, tpl.getSubject(), tpl.getBody(), tpl.getContentType());

    }

    /**
     * A wrapper method for the 'sendEmail' method that extracts
     * the email subject, body, and format from a 'MailTemplate'
     * object and performs variable replacement, given a map of
     * variable-value pairs.
     */
    public static void sendEmail(String from, List<String> to, List<String> cc,
                                 List<String> bcc, MailTemplate tpl, Map<String, String> vars)
                                 throws AddressException, MessagingException {

        String subject = MailVariableResolver.resolve(tpl.getSubject(), vars);
        String body = MailVariableResolver.resolve(tpl.getBody(), vars);

        sendEmail(from, to, cc, bcc, subject, body, tpl.getContentType());
    }
}
