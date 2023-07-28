package replete.collections.tl.events;

import replete.collections.tl.TrackedList;

public class TLReplaceEvent<T> extends TLEvent<T> {


    ////////////
    // FIELDS //
    ////////////

    private int index;
    private T previous;
    private T current;


    //////////////////
    // CONSTRUCTORS //
    //////////////////

    public TLReplaceEvent(TrackedList<T> source, int index, T previous, T current) {
        super(source);
        this.index = index;
        this.previous = previous;
        this.current = current;
    }


    ///////////////
    // ACCESSORS //
    ///////////////

    public int getIndex() {
        return index;
    }
    public T getPrevious() {
        return previous;
    }
    public T getCurrent() {
        return current;
    }


    ////////////////
    // OVERRIDDEN //
    ////////////////

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = super.hashCode();
        result = prime * result + ((current == null) ? 0 : current.hashCode());
        result = prime * result + index;
        result = prime * result + ((previous == null) ? 0 : previous.hashCode());
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
        TLReplaceEvent other = (TLReplaceEvent) obj;
        if(current == null) {
            if(other.current != null) {
                return false;
            }
        } else if(!current.equals(other.current)) {
            return false;
        }
        if(index != other.index) {
            return false;
        }
        if(previous == null) {
            if(other.previous != null) {
                return false;
            }
        } else if(!previous.equals(other.previous)) {
            return false;
        }
        return true;
    }

    @Override
    protected String toStringExtra() {
        return "index=" + index + ",previous=" + previous + ",current=" + current;
    }
}
