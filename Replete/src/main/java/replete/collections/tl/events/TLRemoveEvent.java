package replete.collections.tl.events;

import java.util.Map;

import replete.collections.tl.TrackedList;

public class TLRemoveEvent<T> extends TLEvent<T> {


    ///////////
    // FIELD //
    ///////////

    private Map<Integer, T> removed;


    //////////////////
    // CONSTRUCTORS //
    //////////////////

    public TLRemoveEvent(TrackedList<T> source, Map<Integer, T> removed) {
        super(source);
        this.removed = removed;
    }


    ///////////////
    // ACCESSORS //
    ///////////////

    public Map<Integer, T> getRemoved() {
        return removed;
    }


    ////////////////
    // OVERRIDDEN //
    ////////////////

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = super.hashCode();
        result = prime * result + ((removed == null) ? 0 : removed.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if(this == obj) {
            return true;
        }
        if(!super.equals(obj)) {
            return false;
        }
        if(getClass() != obj.getClass()) {
            return false;
        }
        TLRemoveEvent other = (TLRemoveEvent) obj;
        if(removed == null) {
            if(other.removed != null) {
                return false;
            }
        } else if(!removed.equals(other.removed)) {
            return false;
        }
        return true;
    }

    @Override
    protected String toStringExtra() {
        return "removed=" + removed;
    }
}
