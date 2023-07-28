package replete.collections.tl.events;

import replete.collections.tl.TrackedList;

public abstract class TLEvent<T> {


    ///////////
    // FIELD //
    ///////////

    protected TrackedList<T> source;


    //////////////////
    // CONSTRUCTORS //
    //////////////////

    public TLEvent(TrackedList<T> source) {
        this.source = source;
    }


    ///////////////
    // ACCESSORS //
    ///////////////

    public TrackedList<T> getSource() {
        return source;
    }


    //////////
    // MISC //
    //////////

    protected String toStringExtra() {
        return null;
    }


    ////////////////
    // OVERRIDDEN //
    ////////////////

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((source == null) ? 0 : source.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if(this == obj) {
            return true;
        }
        if(obj == null) {
            return false;
        }
        if(getClass() != obj.getClass()) {
            return false;
        }
        TLEvent other = (TLEvent) obj;
        if(source == null) {
            if(other.source != null) {
                return false;
            }
        } else if(!source.equals(other.source)) {   // TODO: This doesn't verify both sources are TrackedList
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        String extra = toStringExtra();
        if(extra != null) {
            extra = "," + extra;
        } else {
            extra = "";
        }
        return getClass().getSimpleName() + ": [" + source.toStringOriginal() + extra + "]";
    }
}
