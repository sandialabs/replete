package replete.ui.tabbed;

public class TabAboutToCloseEvent {


    ////////////
    // FIELDS //
    ////////////

    private int index;
    private boolean cancel;
    private Object key;


    //////////////////
    // CONSTRUCTORS //
    //////////////////

    public TabAboutToCloseEvent(int index, Object key) {
        this.index = index;
        this.key = key;
        cancel = false;
    }


    //////////////////////////
    // ACCESSORS / MUTATORS //
    //////////////////////////

    public int getIndex() {
        return index;
    }
    public Object getKey() {
        return key;
    }
    public boolean isCanceled() {
        return cancel;
    }

    public void cancel() {
        cancel = true;
    }
}
