package replete.scripting.rscript.evaluation.functions;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.Arrays;

import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;

@Ignore
public abstract class FunctionTest {
    protected abstract Function getFunction();
    private Function function;
    public FunctionTest() {
        function = getFunction();
    }

    @Test(expected = NullPointerException.class)
    public void testNullEval() {
        expectOK(null, null);
    }

    protected void expectOK(Object expectedRet, Object[] args) {
        Object actualRet = function.eval(args);
        assertEquals(expectedRet, actualRet);
    }

    protected void expectOKArray(Object[] expectedRet, Object[] args) {
        Object[] actualRet = (Object[]) function.eval(args);
        assertTrue(Arrays.equals(expectedRet, actualRet));
    }

    protected void expectFail(Object[] args, String expectedError) {
        String input = function.getName() + ", " + Arrays.toString(args);
        try {
            function.eval(args);
            Assert.fail("Expected error but did not find one (" + input + ")");
        } catch(Exception ex) {
            String msg = ex.getMessage();
            BufferedReader reader = new BufferedReader(new StringReader(msg));
            if(msg.contains("\n") || msg.contains("\r")) {
                try {
                    msg = reader.readLine();
                } catch(IOException e) {}
            }
            if(!msg.equals(expectedError)) {
                System.out.println("INPUT=" + input);
                System.out.println("ERR=" + msg);
                Assert.fail("Expected error message '" + expectedError + "' but found '" + msg + "' (" + input + ")");
            }
        }
    }
}
