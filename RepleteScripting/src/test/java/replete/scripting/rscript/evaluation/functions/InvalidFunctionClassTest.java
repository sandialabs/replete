package replete.scripting.rscript.evaluation.functions;

import org.junit.Test;

public class InvalidFunctionClassTest extends FunctionTest {

    @Override
    protected Function getFunction() {
        return new BrokenAdditionFunction();
    }

    @Test
    public void testInvalidReturnValue() {
        expectFail(new Object[] {0, 0},
            "Invalid function return type encountered for '+'.  Found 'String' but expected 'Number'.");
    }

    private class BrokenAdditionFunction extends Function {
        @Override
        public String getName() {
            return "+";
        }
        @Override
        public String getDescription() {
            return "broken arithmetic addition";
        }
        @Override
        public ParameterSet[] getAllowedParameterSets() {
            return new ParameterSet[] {
                new ParameterSet(
                    "!RET", "val1", "val2",
                    Number.class, Number.class, Number.class)
            };
        }
        @Override
        protected Object eval(Object[] args, int parameterTypeIndex) {
            return "to string or not to string";
        }
    }
}
