package replete.pipeline;

public class PipelineExecuteParams {


    ////////////
    // FIELDS //
    ////////////

    private String stageId;
    private boolean from;
    private boolean force;


    //////////////////
    // CONSTRUCTORS //
    //////////////////

    public PipelineExecuteParams(String stageId, boolean from, boolean force) {
        this.stageId = stageId;
        this.from = from;
        this.force = force;
    }


    ///////////////
    // ACCESSORS //
    ///////////////

    public String getStageId() {
        return stageId;
    }
    public boolean isFrom() {
        return from;
    }
    public boolean isForce() {
        return force;
    }
}
