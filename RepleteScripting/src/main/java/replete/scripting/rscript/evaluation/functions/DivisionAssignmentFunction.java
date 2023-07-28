package replete.scripting.rscript.evaluation.functions;

public class DivisionAssignmentFunction extends DivisionFunction {
    @Override
    public String getName() {
        return "/=";
    }

    @Override
    public String getDescription() {
        return "division assignment";
    }

    @Override
    public boolean isAssignment() {
        return true;
    }
}
