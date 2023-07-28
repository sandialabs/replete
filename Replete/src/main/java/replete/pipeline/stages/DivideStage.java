package replete.pipeline.stages;

public class DivideStage implements Stage<Double>, ParameterStage<Double> {

    ////////////
    // FIELDS //
    ////////////

    private static final String name             = "Divide";
    private static final String shortDescription = "input / stage";
    private static final String description      = "Returns the input value divided by the stage value.";

    // Stage field
    private Double divisor;

    //////////////////
    // CONSTRUCTORS //
    //////////////////

    public DivideStage(Double divisor) {
        this.divisor = divisor;
    }
    public DivideStage() {
    }

    //////////////////////////
    // ACCESSORS / MUTATORS //
    //////////////////////////

    public void setDivisor(Double divisor) {
        this.divisor = divisor;
    }
    public Double getDivisor() {
        return divisor;
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
        setDivisor(input);
    }
    @Override
    public Double getValue() {
        return getDivisor();
    }
    @Override
    public Stage<Double> spawnCopy(Double input) {
        return new DivideStage(input);
    }

    /////////////
    // EXECUTE //
    /////////////

    @Override
    public Double execute(Double input) {
        return input / divisor;
    }
}
