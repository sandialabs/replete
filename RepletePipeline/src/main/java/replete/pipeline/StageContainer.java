package replete.pipeline;

import java.util.List;

import replete.pipeline.events.StageContainerListener;

public interface StageContainer {    // StageGraph could almost be a StageContainer too


    //////////////////////////
    // ACCESSORS / MUTATORS //
    //////////////////////////

    // Accessors

    public int getStageCount();
    public List<Stage> getStages();
    public boolean containsStage(Stage stage);

    // Mutators

    public void addStage(Stage stage);
    public void removeStage(Stage stage);


    ///////////////
    // NOTIFIERS //
    ///////////////

    public void addStageAddedListener(StageContainerListener listener);
    public void removeStageAddedListener(StageContainerListener listener);

    public void addStageRemovedListener(StageContainerListener listener);
    public void removeStageRemovedListener(StageContainerListener listener);
}
