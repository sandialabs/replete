package replete.pipeline;

import java.util.ArrayList;
import java.util.List;

import replete.pipeline.stages.Stage;

public class DoublePipeline implements Stage<Double> {

    ////////////
    // FIELDS //
    ////////////
    private static final String name             = "Double Pipeline";
    private static final String shortDescription = "input -> output";
    private static final String description      = "Defines a pipeline for performing mathematical operations on doubles.";

    // Stage field
    private List<Stage<Double>> stages;

    //////////////////
    // CONSTRUCTORS //
    //////////////////

    public DoublePipeline() {
        stages = new ArrayList<>();
    }

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


    //////////////////////
    // STAGE OPERATIONS //
    //////////////////////

    public DoublePipeline addStage(Stage<Double> stage) {
        stages.add(stage);
        return this;
    }

    /////////////
    // EXECUTE //
    /////////////

    @Override
    public Double execute(Double input) {
        Double pipelineState = input;
        for(Stage<Double> stage : stages) {
            pipelineState = stage.execute(pipelineState);
        }
        return pipelineState;
    }
}
