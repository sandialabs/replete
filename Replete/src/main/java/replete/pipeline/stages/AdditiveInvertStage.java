package replete.pipeline.stages;

public class AdditiveInvertStage implements Stage<Double>{

    ////////////
    // FIELDS //
    ////////////

    private static final String name             = "Additive Invert";
    private static final String shortDescription = "input * -1";
    private static final String description      = "Returns the additive inverse of the input value.";

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
        return input * -1.0;
    }
}
