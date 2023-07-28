package replete.collections.tl.events;

import java.util.Map;

import replete.collections.tl.TrackedList;

public class TLAddEvent<T> extends TLEvent<T> {


    ///////////
    // FIELD //
    ///////////

    private Map<Integer, T> added;


    //////////////////
    // CONSTRUCTORS //
    //////////////////

    public TLAddEvent(TrackedList<T> source, Map<Integer, T> added) {
        super(source);
        this.added = added;
    }


    ///////////////
    // ACCESSORS //
    ///////////////

    public Map<Integer, T> getAdded() {
        return added;
    }


    ////////////////
    // OVERRIDDEN //
    ////////////////

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = super.hashCode();
        result = prime * result + ((added == null) ? 0 : added.hashCode());
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
        TLAddEvent other = (TLAddEvent) obj;
        if(added == null) {
            if(other.added != null) {
                return false;
            }
        } else if(!added.equals(other.added)) {
            return false;
        }
        return true;
    }

    @Override
    protected String toStringExtra() {
        return "added=" + added;
    }
}
