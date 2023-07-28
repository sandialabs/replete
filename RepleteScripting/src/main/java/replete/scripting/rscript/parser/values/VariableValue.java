package replete.scripting.rscript.parser.values;

public class VariableValue extends NodeValue {
    private String name;

    public VariableValue(String name) {
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
