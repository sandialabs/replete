package replete.pipeline;

import replete.event.ExtChangeNotifier;
import replete.pipeline.events.OutputChangeEvent;
import replete.pipeline.events.StageContainerListener;

// This class is NOT actually used, because Pipeline cannot inherit
// from both AbstractStage and AbstractStageContainer.  However,
// this class can still serve to show the notifiers for a StageContainer.
// It can also be used for other non-Stage StageContainer's.  It's also
// possible to make EVERY Stage a StageContainer - this would work
// well with the getParent():Stage design.

public abstract class AbstractStageContainer implements StageContainer {


    ///////////////
    // NOTIFIERS //
    ///////////////

    // Stage added

    private ExtChangeNotifier<StageContainerListener> stageAddedNotifier = new ExtChangeNotifier<StageContainerListener>();
    @Override
    public void addStageAddedListener(StageContainerListener listener) {
        stageAddedNotifier.addListener(listener);
    }
    @Override
    public void removeStageAddedListener(StageContainerListener listener) {
        stageAddedNotifier.removeListener(listener);
    }
    protected void fireStageAddedEvent(OutputChangeEvent e) {
        stageAddedNotifier.fireStateChanged(e);
    }

    // Stage removed

    private ExtChangeNotifier<StageContainerListener> stageRemovedNotifier = new ExtChangeNotifier<StageContainerListener>();
    @Override
    public void addStageRemovedListener(StageContainerListener listener) {
        stageRemovedNotifier.addListener(listener);
    }
    @Override
    public void removeStageRemovedListener(StageContainerListener listener) {
        stageRemovedNotifier.removeListener(listener);
    }
    protected void fireStageRemovedEvent(OutputChangeEvent e) {
        stageRemovedNotifier.fireStateChanged(e);
    }
}
