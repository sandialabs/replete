package replete.scripting.rscript.evaluation.functions;

public class ModulusAssignmentFunction extends ModulusFunction {
    @Override
    public String getName() {
        return "%=";
    }

    @Override
    public String getDescription() {
        return "modulus assignment";
    }

    @Override
    public boolean isAssignment() {
        return true;
    }
}
