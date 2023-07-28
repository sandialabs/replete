package replete.scripting.rscript.evaluation.functions;

public class MultiplicationAssignmentFunction extends MultiplicationFunction {
    @Override
    public String getName() {
        return "*=";
    }

    @Override
    public String getDescription() {
        return "multiplication assignment";
    }

    @Override
    public boolean isAssignment() {
        return true;
    }
}
