package replete.pipeline.stages;

public class LogBaseTwoStage implements Stage<Double> {

    ////////////
    // FIELDS //
    ////////////

    private static final String name             = "Log Base Two";
    private static final String shortDescription = "log2(input)";
    private static final String description      = "Returns a number such that 2 raised to that number equals the input value.";

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
        return Math.log(input) / Math.log(2.0);
    }
}
