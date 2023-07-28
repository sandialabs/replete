package replete.pipeline.stages;

public class ExponentiateStage implements Stage<Double>, ParameterStage<Double> {

    ////////////
    // FIELDS //
    ////////////

    private static final String name             = "Exponentiate";
    private static final String shortDescription = "input ^ output";
    private static final String description      = "Returns the input value raised to the stage value.";

    // Stage field
    private Double exponent;

    //////////////////
    // CONSTRUCTORS //
    //////////////////

    public ExponentiateStage(Double exponent) {
        this.exponent = exponent;
    }
    public ExponentiateStage() {
    }

    //////////////////////////
    // ACCESSORS / MUTATORS //
    //////////////////////////

    public Double getExponent() {
        return exponent;
    }
    public void setExponent(Double exponent) {
        this.exponent = exponent;
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
        setExponent(input);
    }
    @Override
    public Double getValue() {
        return getExponent();
    }
    @Override
    public Stage<Double> spawnCopy(Double input) {
        return new ExponentiateStage(input);
    }

    /////////////
    // EXECUTE //
    /////////////

    @Override
    public Double execute(Double input) {
        return Math.pow(input, exponent);
    }
}
