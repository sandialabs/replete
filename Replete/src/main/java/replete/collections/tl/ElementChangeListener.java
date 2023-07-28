package replete.collections.tl;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

public class ElementChangeListener<T> implements ChangeListener {


    ////////////
    // FIELDS //
    ////////////

    private TrackedList<T> source;
    private int index;
    private boolean invalid = false;


    //////////////////
    // CONSTRUCTORS //
    //////////////////

    public ElementChangeListener(TrackedList<T> source, int i) {
        this.source = source;
        index = i;
    }


    //////////////////////////
    // ACCESSORS / MUTATORS //
    //////////////////////////

    // Accessors

    public TrackedList<T> getSource() {
        return source;
    }
    public int getIndex() {
        return index;
    }

    // Mutators

    // This is so we can reuse these listener objects.
    // [OPTION] Make these objects immutable.
    public void setIndex(int index) {
        this.index = index;
    }
    public void invalidate() {
        invalid = true;
    }


    //////////
    // MISC //
    //////////

    public void stateChanged(ChangeEvent e) {
        source.listenerCallback(this, invalid, index);
    }


    ////////////////
    // OVERRIDDEN //
    ////////////////

    @Override
    public String toString() {
        return "ElementChangeListener[" + index + "]";
    }
}
