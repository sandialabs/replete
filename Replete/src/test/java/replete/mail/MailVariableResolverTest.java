package replete.mail;

import static org.junit.Assert.assertEquals;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.junit.Test;

public class MailVariableResolverTest {

    @Test
    public void test1() {

        Map<String, String> vars = new HashMap<String, String>();

        vars.put("fullName", "Derek Trumbo");
        vars.put("closing", "Sincerely");
        vars.put("1", "Saturn");

        String text = "Dear ${fullName},\n\nYou have been selected as a winner!\n\n${closing},\nPublishers Clearing House${1}${1}";

        String expected = "Dear Derek Trumbo,\n\nYou have been selected as a winner!\n\nSincerely,\nPublishers Clearing HouseSaturnSaturn";

        assertEquals(expected, MailVariableResolver.resolve(text, vars));

        Set<String> extractedVars = MailVariableResolver.findVariables("${name} sat on a ${object} and ${name} had a great fall.");

        assertEquals("[name, object]", extractedVars.toString());
    }

}
