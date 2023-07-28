package replete.pipeline.stages;

public class LogBaseEStage implements Stage<Double> {

    ////////////
    // FIELDS //
    ////////////

    private static final String name             = "Natural Log";
    private static final String shortDescription = "ln(input)";
    private static final String description      = "Returns a number such that e raised to that number equals the input value.";

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
        return Math.log(input);
    }

}
