package replete.pipeline.stages;

public class SquareStage implements Stage<Double> {

    ////////////
    // FIELDS //
    ////////////

    private static final String name             = "Square";
    private static final String shortDescription = "input ^ 2";
    private static final String description      = "Returns the input value squared.";

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
        return input * input;
    }
}
