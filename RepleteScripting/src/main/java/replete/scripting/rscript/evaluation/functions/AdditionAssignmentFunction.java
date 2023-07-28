package replete.scripting.rscript.evaluation.functions;

public class AdditionAssignmentFunction extends AdditionFunction {
    @Override
    public String getName() {
        return "+=";
    }

    @Override
    public String getDescription() {
        return "addition assignment";
    }

    @Override
    public boolean isAssignment() {
        return true;
    }
}
