package replete.pipeline.stages;

public class MultiplicativeInvertStage implements Stage<Double> {

    ////////////
    // FIELDS //
    ////////////

    private static final String name             = "Multiplicative Inverse";
    private static final String shortDescription = "1 / input";
    private static final String description      = "Returns the input value raised to -1.";

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
        return 1.0 / input;
    }
}
