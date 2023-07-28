package replete.collections.tl.events;

import replete.collections.tl.TrackedList;


public class TLClearEvent<T> extends TLEvent<T> {


    //////////////////
    // CONSTRUCTORS //
    //////////////////

    public TLClearEvent(TrackedList<T> source) {
        super(source);
    }
}
