package replete.pipeline.stages;

public class AddStage implements Stage<Double>, ParameterStage<Double> {

    ////////////
    // FIELDS //
    ////////////

    private static final String name             = "Add";
    private static final String shortDescription = "input + stage";
    private static final String description      = "Returns the stage value added to the input value.";

    // Stage field
    private Double summand;

    //////////////////
    // CONSTRUCTORS //
    //////////////////

    public AddStage(Double summand) {
        this.summand = summand;
    }
    public AddStage() {
    }

    //////////////////////////
    // ACCESSORS / MUTATORS //
    //////////////////////////

    public void setSummand(Double summand) {
        this.summand = summand;
    }
    public Double getSummand() {
        return summand;
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
        setSummand(input);
    }
    @Override
    public Double getValue() {
        return getSummand();
    }
    @Override
    public Stage<Double> spawnCopy(Double input) {
        return new AddStage(input);
    }

    /////////////
    // EXECUTE //
    /////////////

    @Override
    public Double execute(Double input) {
        return input + summand;
    }
}
