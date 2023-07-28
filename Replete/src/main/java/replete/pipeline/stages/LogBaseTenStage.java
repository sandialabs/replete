package replete.pipeline.stages;

public class LogBaseTenStage implements Stage<Double> {

    ////////////
    // FIELDS //
    ////////////

    private static final String name             = "Log 10";
    private static final String shortDescription = "log10(input)";
    private static final String description      = "Returns a number such that 10 raised to that number equals the input value.";

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
        return Math.log10(input);
    }
}
