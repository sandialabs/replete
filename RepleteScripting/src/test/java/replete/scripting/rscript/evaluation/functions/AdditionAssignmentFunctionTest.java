package replete.scripting.rscript.evaluation.functions;

import org.junit.Test;

public class AdditionAssignmentFunctionTest extends FunctionTest {
    @Override
    protected Function getFunction() {
        return new AdditionAssignmentFunction();
    }

    @Test
    public void testOperator() {
        expectFail(new Object[] {},
            "Invalid function arguments supplied for '+='.  Function not applicable for ().");
        expectFail(new Object[] {0},
            "Invalid function arguments supplied for '+='.  Function not applicable for (Integer).");
        expectOK(0.0, new Object[] {0, 0});
    }
}
