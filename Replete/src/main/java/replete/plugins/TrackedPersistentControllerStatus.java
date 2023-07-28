package replete.plugins;

import java.util.UUID;

public class TrackedPersistentControllerStatus extends PersistentControllerStatus {


    ////////////
    // FIELDS //
    ////////////

    private UUID trackedId;


    //////////////////
    // CONSTRUCTORS //
    //////////////////

    public TrackedPersistentControllerStatus(TrackedPersistentController controller) {
        super(controller);
        trackedId = controller.getTrackedId();
    }
    public TrackedPersistentControllerStatus(TrackedPersistentControllerStatus status) {
        super(status);
        trackedId = status.trackedId;
    }


    ///////////////
    // ACCESSORS //
    ///////////////

    public UUID getTrackedId() {
        return trackedId;
    }
}
