package replete.scripting.rscript.evaluation.functions;

public class SubtractionAssignmentFunction extends SubtractionFunction {
    @Override
    public String getName() {
        return "-=";
    }

    @Override
    public String getDescription() {
        return "subtraction assignment";
    }

    @Override
    public boolean isAssignment() {
        return true;
    }
}
