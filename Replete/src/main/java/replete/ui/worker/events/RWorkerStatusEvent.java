package replete.ui.worker.events;

import replete.ui.worker.RWorker;
import replete.ui.worker.RWorkerStatus;

public class RWorkerStatusEvent extends RWorkerEvent {


    ////////////
    // FIELDS //
    ////////////

    private RWorkerStatus previous;
    private RWorkerStatus current;


    //////////////////
    // CONSTRUCTORS //
    //////////////////

    public RWorkerStatusEvent(RWorker source, RWorkerStatus previous, RWorkerStatus current) {
        super(source);
        this.previous = previous;
        this.current = current;
    }


    ///////////////
    // ACCESSORS //
    ///////////////

    public RWorkerStatus getPrevious() {
        return previous;
    }
    public RWorkerStatus getCurrent() {
        return current;
    }


    //////////////
    // toString //
    //////////////

    @Override
    public String toString() {
        return "StatusChange[" + previous + " => " + current + "]";
    }
}
