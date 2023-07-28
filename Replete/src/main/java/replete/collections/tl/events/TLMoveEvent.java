package replete.collections.tl.events;

import java.util.Map;

import replete.collections.tl.TrackedList;

public class TLMoveEvent<T> extends TLEvent<T> {


    ////////////
    // FIELDS //
    ////////////

    private Map<Integer, T> moved;
    private Map<Integer, Integer> curIdxToPrevIdx;


    //////////////////
    // CONSTRUCTORS //
    //////////////////

    public TLMoveEvent(TrackedList<T> source, Map<Integer, T> moved, Map<Integer, Integer> curIdxToPrevIdx) {
        super(source);
        this.moved = moved;
        this.curIdxToPrevIdx = curIdxToPrevIdx;
    }


    ///////////////
    // ACCESSORS //
    ///////////////

    public Map<Integer, T> getMoved() {
        return moved;
    }
    public Map<Integer, Integer> getCurIdxToPrevIdx() {
        return curIdxToPrevIdx;
    }


    ////////////////
    // OVERRIDDEN //
    ////////////////

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = super.hashCode();
        result = prime * result + ((curIdxToPrevIdx == null) ? 0 : curIdxToPrevIdx.hashCode());
        result = prime * result + ((moved == null) ? 0 : moved.hashCode());
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
        TLMoveEvent other = (TLMoveEvent) obj;
        if(curIdxToPrevIdx == null) {
            if(other.curIdxToPrevIdx != null) {
                return false;
            }
        } else if(!curIdxToPrevIdx.equals(other.curIdxToPrevIdx)) {
            return false;
        }
        if(moved == null) {
            if(other.moved != null) {
                return false;
            }
        } else if(!moved.equals(other.moved)) {
            return false;
        }
        return true;
    }

    @Override
    protected String toStringExtra() {
        return "moved=" + moved + ",curIdxToPrevIdx=" + curIdxToPrevIdx;
    }
}
