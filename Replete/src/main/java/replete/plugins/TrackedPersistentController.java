package replete.plugins;

import java.util.UUID;

import replete.text.StringUtil;

// Not sure if "tracked ID" functionality should just be a part of
// all persistent controllers or not.  Often, if a persistent
// controller is even in existence, running in the background,
// persistently controlling or whatever, and if it can be reparam-
// eterized constantly, it might be quite common to need to
// match parameters with the persistent controller that they are
// for.  But in simpler uses of persistent controllers, I
// suppose perhaps that would no be necessary.  So this class
// exists currently to add just a little bit more functionality
// to the overall persistent controller idea.

public abstract class TrackedPersistentController<P, S> extends PersistentController<P, S> {


    ////////////
    // FIELDS //
    ////////////

    protected UUID trackedId;


    //////////////////
    // CONSTRUCTORS //
    //////////////////

    public TrackedPersistentController(P params, UUID trackedId) {
        super(params);
        this.trackedId = trackedId;
    }


    ///////////////
    // ACCESSORS //
    ///////////////

    public UUID getTrackedId() {
        return trackedId;
    }


    //////////
    // MISC //
    //////////

    public String getDebugString() {
        int width = (getClass().getSimpleName() + ":").length();
        return getDebugString(width);
    }
    public String getDebugString(int width) {
        return String.format("%-" + width + "s %s (%s)",
            getClass().getSimpleName() + ":",
            getTrackedId(),
            StringUtil.padLeft(Integer.toHexString(hashCode()), '0', 8)
        );
    }
}
