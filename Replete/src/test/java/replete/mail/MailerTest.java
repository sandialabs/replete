package replete.mail;

import static org.junit.Assert.fail;

import java.util.Arrays;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

public class MailerTest {

    private static final String DEFAULT_ADDR = "dtrumbo@sandia.gov";
    private static final String OVERRIDE_ADDR = "dtrumbo@sandia.gov";

    // Not all overloaded Mailer.sendMail methods are
    // tested here.  Only the primary one is tested.
    // This is because the other methods are tested
    // indirectly by other JUnit tests.

    @Before
    public void setUp() {

        // Initialize static members of the Mailer class.
        Mailer.setDefaultFormat(Mailer.FORMAT_PLAIN);
        Mailer.setTestMode(false);
    }

    @Test
    public void plainText() {
        String from = DEFAULT_ADDR;
        String to = DEFAULT_ADDR;
        String cc = DEFAULT_ADDR;
        String subject = "JUnit Test Email - plainText";
        String body = "<b>earth</b><br><i>wind</i>";
        try {
            Mailer.sendEmail(from, list(to), list(cc), null, subject, body);
        } catch(Exception e) {
            e.printStackTrace();
            fail();
        }
    }

    // Commenting these tests out until we can properly separate the tests
    // that create email bodies vs. testing that AN e-mail can be sent.
    // We don't need to send over a half-dozen e-mails every time these
    // unit tests are executed.  Each e-mail has always taken 5-10 seconds
    // to send and thus sending all of these has unnecessarily added to 
    // the execution of our unit tests.
//    @Test
//    public void html() {
//        String from = DEFAULT_ADDR;
//        String to = DEFAULT_ADDR;
//        String cc = DEFAULT_ADDR;
//        String subject = "JUnit Test Email - html";
//        String body = "<b>earth</b><br><i>wind</i>";
//        Mailer.setDefaultFormat(Mailer.FORMAT_HTML);
//        try {
//            Mailer.sendEmail(from, list(to), list(cc), null, subject, body);
//        } catch(Exception e) {
//            fail();
//        }
//    }
//
//    @Test
//    public void quotedNames() {
//        String from = "\"The System\" <" + DEFAULT_ADDR + ">";
//        String to = "\"Last, First\" <" + DEFAULT_ADDR + ">";      // Sandia mail system ignores this quoted name if @sandia.gov.
//        String cc = "\"This Other Guy\" <" + DEFAULT_ADDR + ">";   // Sandia mail system ignores this quoted name if @sandia.gov.
//        String subject = "JUnit Test Email - quotedNames";
//        String body = "<b>earth</b><br><i>wind</i>";
//        Mailer.setDefaultFormat(Mailer.FORMAT_HTML);
//        try {
//            Mailer.sendEmail(from, list(to), list(cc), null, subject, body);
//        } catch(Exception e) {
//            fail();
//        }
//    }
//
//    @Test
//    public void testModePlain() {
//        String from = DEFAULT_ADDR;
//        String to = DEFAULT_ADDR;
//        String cc = DEFAULT_ADDR;
//        String subject = "JUnit Test Email - testModePlain";
//        String body = "<b>earth</b><br><i>wind</i>";
//        Mailer.setTestMode(true);
//        Mailer.setOverrideTo(list(OVERRIDE_ADDR));
//        Mailer.setOverrideCC(list(OVERRIDE_ADDR));
//        try {
//            Mailer.sendEmail(from, list(to), list(cc), null, subject, body);                 // Does not not return until complete
//        } catch(Exception e) {
//            fail();
//        }
//    }
//
//    @Test
//    public void testModeHTML() {
//        String from = DEFAULT_ADDR;
//        String to = DEFAULT_ADDR;
//        String cc = DEFAULT_ADDR;
//        String subject = "JUnit Test Email - testModeHTML";
//        String body = "<b>earth</b><br><i>wind</i>";
//        Mailer.setDefaultFormat(Mailer.FORMAT_HTML);
//        Mailer.setTestMode(true);
//        Mailer.setOverrideTo(list(DEFAULT_ADDR));
//        Mailer.setOverrideCC(list(DEFAULT_ADDR));
//        try {
//            Mailer.sendEmail(from, list(to), list(cc), null, subject, body);                 // Does not not return until complete
//        } catch(Exception e) {
//            fail();
//        }
//    }
//
//    @Test(expected=IllegalArgumentException.class)
//    public void testNullFrom() {
//        String from = null;
//        String to = DEFAULT_ADDR;
//        String cc = DEFAULT_ADDR;
//        String subject = "JUnit Test Email - testModeHTML";
//        String body = "<b>earth</b><br><i>wind</i>";
//        Mailer.setDefaultFormat(Mailer.FORMAT_HTML);
//        try {
//            Mailer.sendEmail(from, list(to), list(cc), null, subject, body);                 // Does not not return until complete
//        } catch(AddressException e) {
//        } catch(MessagingException e) {
//        }
//    }
//
//    @Test(expected=IllegalArgumentException.class)
//    public void testNullTo() {
//        String from = DEFAULT_ADDR;
//        String to = null;
//        String cc = DEFAULT_ADDR;
//        String subject = "JUnit Test Email - testModeHTML";
//        String body = "<b>earth</b><br><i>wind</i>";
//        Mailer.setDefaultFormat(Mailer.FORMAT_HTML);
//        try {
//            Mailer.sendEmail(from, list(to), list(cc), null, subject, body);                 // Does not not return until complete
//        } catch(AddressException e) {
//        } catch(MessagingException e) {
//        }
//    }
//
//    @Test
//    public void testSpecialCharacterVars() {
//        String from = DEFAULT_ADDR;
//        String to = DEFAULT_ADDR;
//        String cc = DEFAULT_ADDR;
//        String subject = "JUnit Test Email - testModeHTML";
//        String body = "<b>earth</b><br><i>wind</i> ${next}";
//        Mailer.setDefaultFormat(Mailer.FORMAT_HTML);
//        try {
//            Map<String, String> vars = new HashMap<>();
//            vars.put("next", "$f\\ire\\");
//            Mailer.sendEmail(from, list(to), list(cc), null, subject, body, vars);                 // Does not not return until complete
//        } catch(AddressException e) {
//        } catch(MessagingException e) {
//        }
//    }

    private static List<String> list(String e) {
        if(e == null) {
            return null;
        }
        return Arrays.asList(new String[] {e});
    }
}
