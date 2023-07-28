package replete.scripting.rscript.evaluation.functions;

public class ExponentiationAssignmentFunction extends ExponentiationFunction {
    @Override
    public String getName() {
        return "**=";
    }

    @Override
    public String getDescription() {
        return "exponentiation assignment";
    }

    @Override
    public boolean isAssignment() {
        return true;
    }
}
