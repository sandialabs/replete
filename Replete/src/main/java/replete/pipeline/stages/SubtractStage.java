package replete.pipeline.stages;

public class SubtractStage implements Stage<Double>, ParameterStage<Double> {

    ////////////
    // FIELDS //
    ////////////

    private static final String name             = "Subtraction";
    private static final String shortDescription = "input - stage";
    private static final String description      = "Returns the input value minus the stage value.";

    // Stage field
    private Double subtrahend;

    //////////////////
    // CONSTRUCTORS //
    //////////////////

    public SubtractStage(Double subtrahend) {
        this.subtrahend = subtrahend;
    }
    public SubtractStage() {
    }

    //////////////////////////
    // ACCESSORS / MUTATORS //
    //////////////////////////

    public void setSubtrahend(Double subtrahend) {
        this.subtrahend = subtrahend;
    }
    public Double getSubtrahend() {
        return subtrahend;
    }
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
    @Override
    public void setValue(Double input) {
        setSubtrahend(input);
    }
    @Override
    public Double getValue() {
        return getSubtrahend();
    }
    @Override
    public Stage<Double> spawnCopy(Double input) {
        return new SubtractStage(input);
    }
    /////////////
    // EXECUTE //
    /////////////

    @Override
    public Double execute(Double input) {
        return input - subtrahend;
    }
}