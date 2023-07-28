package replete.ui.sp;

public class LineNumberRange implements Comparable<LineNumberRange> {


    ////////////
    // FIELDS //
    ////////////

    public int start;
    public int endNonIncl;


    //////////////////
    // CONSTRUCTORS //
    //////////////////

    public LineNumberRange(int line) {
        this(line, line + 1);
    }
    public LineNumberRange(int start, int endNonIncl) {
        this.start = start;
        this.endNonIncl = endNonIncl;
    }


    //////////////////////////
    // ACCESSORS / MUTATORS //
    //////////////////////////

    public int getStart() {
        return start;
    }
    public int getEndNonIncl() {
        return endNonIncl;
    }


    ////////////////
    // OVERRIDDEN //
    ////////////////

    @Override
    public int compareTo(LineNumberRange o) {
        return start - o.start;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + endNonIncl;
        result = prime * result + start;
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
        LineNumberRange other = (LineNumberRange) obj;
        if(endNonIncl != other.endNonIncl) {
            return false;
        }
        if(start != other.start) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "LineNumberRange [start=" + start + ", endNonIncl=" + endNonIncl + "]";
    }
}
