package replete.collections.tl.events;

import replete.collections.tl.TrackedList;


public class TLChangeEvent<T> extends TLEvent<T> {


    //////////////////
    // CONSTRUCTORS //
    //////////////////

    public TLChangeEvent(TrackedList<T> source) {
        super(source);
    }
}
