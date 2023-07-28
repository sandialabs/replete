package replete.pipeline.stages;

public class SquareRootStage implements Stage<Double> {

    ////////////
    // FIELDS //
    ////////////

    private static final String name             = "Square Root";
    private static final String shortDescription = "sqrt(input)";
    private static final String description      = "Returns the square root of the input value.";

    //////////////////////////
    // ACCESSORS / MUTATORS //
    //////////////////////////

    @Override
    public String getName() {
        return name;
    }
    @Override
    public String getShortDescription() {
        return shortDescription;
    }
    @Override
    public String getDescription() {
        return description;
    }

    /////////////
    // EXECUTE //
    /////////////

    @Override
    public Double execute(Double input) {
        return Math.sqrt(input);
    }
}
