package replete.jgraph.test;

import replete.pipeline.Stage;

public class StageWrapper {


    ///////////
    // FIELD //
    ///////////

    private Stage stage;


    /////////////////
    // CONSTRUCTOR //
    /////////////////

    public StageWrapper(Stage stage) {
        this.stage = stage;
    }


    ///////////////
    // ACCESSORS //
    ///////////////

    public Stage getStage() {
        return stage;
    }


    ////////////////
    // OVERRIDDEN //
    ////////////////

    @Override
    public String toString() {
        return "Moving " + stage.getName() + "...";
    }
}
