package replete.ui;

/**
 * This interface is to be employed on objects which you
 * need to know whether they've changed since construction
 * or relatedly since they've been read from a file on disk.
 * These objects are commonly related to the GUI in that the
 * user is the main driver behind when the object might be
 * changed (for example a text file inside an editor has a
 * dirty state).  If an object is dirty and the user performs
 * some action what would discard that object (close object
 * or exit program), then the user is usually be prompted as
 * to whether they want to 1) save the object and continue,
 * 2) do not save the object and continue, or 3) cancel the
 * operation that was going to discard the object.  Classes
 * that implement this interface should maintain a boolean
 * instance variable that represents this dirty state.  This
 * boolean must be false by the end of each constructor, and
 * be set to true each time a mutator (setter) is called to
 * change some part of the object.  The mutator can decide to
 * check whether the value being set differs from the current
 * value and not set the dirty state if the values are equal.
 * This boolean field should should be transient and not
 * written out to a file if the object is serialized.  The
 * boolean may be set to false when the object is saved to
 * disk.  One other nuance is if an object whose dirty
 * state is being tracked, sometimes the object's boolean
 * must be set to true when other objects that the object
 * references change.
 *
 * @author Derek Trumbo
 */

public interface Dirtyable {
    public boolean isDirty();
    public void setDirty(boolean dirty);
}
