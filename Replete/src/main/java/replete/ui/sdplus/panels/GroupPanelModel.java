package replete.ui.sdplus.panels;

/**
 * The model which backs a group of scale panels in the
 * scale set panel.
 *
 * @author Derek Trumbo
 */

public class GroupPanelModel {

    ////////////
    // Fields //
    ////////////

    protected boolean open;

    protected String name;
    protected String note;

    //////////////////
    // CONSTRUCTORS //
    //////////////////

    // Default settings for a new scale panel:
    // - Closed
    public GroupPanelModel(String nm, String nt) {
        open = false;

        name = nm;
        note = nt;
    }

    //////////////////////////
    // Accessors / Mutators //
    //////////////////////////

    // Accessors

    public boolean isOpen() {
        return open;
    }
    public String getName() {
        return name;
    }
    public String getNote() {
        return note;
    }

    // Mutators

    public void setOpen(boolean op) {
        open = op;
    }
    public void setName(String nm) {
        name = nm;
    }
    public void setNote(String nt) {
        note = nt;
    }

    //////////////
    // toString //
    //////////////

    @Override
    public String toString() {
        return getClass().getSimpleName() + "[" + internalString() + "]";
    }

    protected String internalString() {
        return "name=" + name +
            ", notes=" + note +
            ", open=" + open;
    }
}
