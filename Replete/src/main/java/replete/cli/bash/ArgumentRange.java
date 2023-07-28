package replete.cli.bash;

public class ArgumentRange {


    ////////////
    // FIELDS //
    ////////////

    private int start;
    private int endNonIncl = -1;


    //////////////////
    // CONSTRUCTORS //
    //////////////////

    public ArgumentRange(int start) {
        this.start = start;
    }


    //////////////////////////
    // ACCESSORS / MUTATORS //
    //////////////////////////

    // Accessors

    public int getStart() {
        return start;
    }
    public int getEndNonIncl() {
        return endNonIncl;
    }

    // Mutators

    public void setEndNonIncl(int endNonIncl) {
        this.endNonIncl = endNonIncl;
    }


    ////////////////
    // OVERRIDDEN //
    ////////////////

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
        ArgumentRange other = (ArgumentRange) obj;
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
        return "ArgumentRange[start=" + start + ",endNonIncl=" + endNonIncl + "]";
    }

    public String toSimpleString() {
        return "[" + start + "," + endNonIncl + "]";
    }
}
