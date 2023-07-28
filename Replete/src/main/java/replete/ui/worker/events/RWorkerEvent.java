package replete.ui.worker.events;

import replete.ui.worker.RWorker;

public class RWorkerEvent {


    ////////////
    // FIELDS //
    ////////////

    private RWorker source;


    //////////////////
    // CONSTRUCTORS //
    //////////////////

    public RWorkerEvent(RWorker source) {
        this.source = source;
    }


    ///////////////
    // ACCESSORS //
    ///////////////

    public RWorker getSource() {
        return source;
    }
}
