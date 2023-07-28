package replete.mail;

import static org.junit.Assert.fail;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Test;

import replete.mail.tpl.ExampleTemplate;


public class MailTemplateTest {

    private static final String DEFAULT_ADDR = "test@test.com";

    @Test
    public void template() {
        MailTemplate confirmTpl = new ExampleTemplate();

        Map<String, String> vars = new HashMap<>();
        vars.put(ExampleTemplate.SUBJECT, "ABC");
        vars.put(ExampleTemplate.NAME, "Obama");
        vars.put(ExampleTemplate.SIG, "The Cat in the Hat");

        try {

            Mailer.sendEmail(DEFAULT_ADDR, list(DEFAULT_ADDR), null, null,
                confirmTpl, vars);

        } catch(Exception e) {
            fail();
        }
    }

    public static List<String> list(String e) {
        if(e == null) {
            return null;
        }
        return Arrays.asList(new String[] {e});
    }
}
