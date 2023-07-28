package replete.collections.tl.events;

import replete.collections.tl.TrackedList;

public class TLElementChangeEvent<T> extends TLEvent<T> {


    ////////////
    // FIELDS //
    ////////////

    private int index;
    private T element;


    //////////////////
    // CONSTRUCTORS //
    //////////////////

    public TLElementChangeEvent(TrackedList<T> source, int index, T element) {
        super(source);
        this.index = index;
        this.element = element;
    }


    ///////////////
    // ACCESSORS //
    ///////////////

    public int getIndex() {
        return index;
    }
    public T getElement() {
        return element;
    }


    ////////////////
    // OVERRIDDEN //
    ////////////////

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = super.hashCode();
        result = prime * result + ((element == null) ? 0 : element.hashCode());
        result = prime * result + index;
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
        TLElementChangeEvent other = (TLElementChangeEvent) obj;
        if(element == null) {
            if(other.element != null) {
                return false;
            }
        } else if(!element.equals(other.element)) {
            return false;
        }
        if(index != other.index) {
            return false;
        }
        return true;
    }

    @Override
    protected String toStringExtra() {
        return "index=" + index + ",element=" + element;
    }
}
