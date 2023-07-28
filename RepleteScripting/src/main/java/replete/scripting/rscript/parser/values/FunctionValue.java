package replete.scripting.rscript.parser.values;

public class FunctionValue extends NodeValue {
    private String name;

    public FunctionValue(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return name;
    }
}
