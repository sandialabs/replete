package replete.collections;

import java.util.UUID;

import replete.plugins.SerializableEmptyEqualsObject;

public class TrackedBean extends SerializableEmptyEqualsObject {


    ////////////
    // FIELDS //
    ////////////

    protected UUID trackedId;


    //////////////////
    // CONSTRUCTORS //
    //////////////////

    public TrackedBean() {
        this(UUID.randomUUID());
    }
    public TrackedBean(UUID trackedId) {
        this.trackedId = trackedId;
    }


    //////////////////////////
    // ACCESSORS / MUTATORS //
    //////////////////////////

    // Accessors

    public UUID getTrackedId() {
        return trackedId;
    }

    // Mutators

    public TrackedBean setTrackedId(UUID trackedId) {
        this.trackedId = trackedId;
        return this;
    }


    ////////////////
    // OVERRIDDEN //
    ////////////////

    // hashCode & equals are not implemented here because the idea behind
    // the tracked bean concept is that it is just used to track instances of
    // individual objects when they are serialized to external destinations,
    // as the identity information (e.g. memory addresses) of objects
    // are not generally serialized to disk.  Thus if we wanted to compare
    // if any two given instances of the same type of object are the same,
    // we generally don't compare instance-related information.

}
