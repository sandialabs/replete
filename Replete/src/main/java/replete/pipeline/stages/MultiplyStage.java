package replete.pipeline.stages;

public class MultiplyStage implements Stage<Double>, ParameterStage<Double> {

    ////////////
    // FIELDS //
    ////////////

    private static final String name             = "Multiply";
    private static final String shortDescription = "input * stage";
    private static final String description      = "Returns the input value multiplied by the stage value.";

    // Stage field
    private Double multiplicand;

    //////////////////
    // CONSTRUCTORS //
    //////////////////

    public MultiplyStage(Double mulitplyBy) {
        this.multiplicand = mulitplyBy;
    }
    public MultiplyStage() {
    }

    //////////////////////////
    // ACCESSORS / MUTATORS //
    //////////////////////////

    public Double getMultiplicand() {
        return multiplicand;
    }
    public void setMulitplicand(Double multiplicand) {
        this.multiplicand = multiplicand;
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
        setMulitplicand(input);
    }
    @Override
    public Double getValue() {
        return getMultiplicand();
    }
    @Override
    public Stage<Double> spawnCopy(Double input) {
        return new MultiplyStage(input);
    }

    /////////////
    // EXECUTE //
    /////////////

    @Override
    public Double execute(Double input) {
        return input * multiplicand;
    }
}
