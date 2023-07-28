package replete.pipeline.stages;

//Need log base 2
public class LogStage implements Stage<Double>, ParameterStage<Double> {

    ////////////
    // FIELDS //
    ////////////

    private static final String name             = "Log";
    private static final String shortDescription = "logbase(input)";
    private static final String description      = "Returns a number such that the input value raised to the returned number equals the stage value.";

    // Stage field
    private Double base;

    //////////////////
    // CONSTRUCTORS //
    //////////////////

    public LogStage(Double base) {
        this.base = base;
    }
    public LogStage() {
    }

    //////////////////////////
    // ACCESSORS / MUTATORS //
    //////////////////////////

    public Double getBase() {
        return base;
    }
    public void setBase(Double base) {
        this.base = base;
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
        setBase(input);
    }
    @Override
    public Double getValue() {
        return getBase();
    }
    @Override
    public Stage<Double> spawnCopy(Double input) {
        return new LogStage(input);
    }

    /////////////
    // EXECUTE //
    /////////////

    @Override
    public Double execute(Double input) {
        return Math.log(input) / Math.log(base);
    }
}
