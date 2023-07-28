package replete.pipeline.stages;

public class RootStage implements Stage<Double>, ParameterStage<Double> {

    ////////////
    // FIELDS //
    ////////////

    private static final String name             = "Root";
    private static final String shortDescription = "input ^ (1 / stage)";
    private static final String description      = "Returns a number such that the returned number raised to the stage value equals the input value.";

    // Stage field
    private Double index;

    //////////////////
    // CONSTRUCTORS //
    //////////////////

    public RootStage(Double index) {
        this.index = index;
    }
    public RootStage() {
    }

    //////////////////////////
    // ACCESSORS / MUTATORS //
    //////////////////////////


    public Double getIndex() {
        return index;
    }
    public void setIndex(Double index) {
        this.index = index;
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
        setIndex(input);
    }
    @Override
    public Double getValue() {
        return getIndex();
    }
    @Override
    public Stage<Double> spawnCopy(Double input) {
        return new RootStage(input);
    }

    /////////////
    // EXECUTE //
    /////////////

    @Override
    public Double execute(Double input) {
        // Downside of this method: it refuses to produce the
        // roots of negative numbers, even when real, non complex ones exist.
        return Math.pow(input, (1.0 / index));
    }
}
