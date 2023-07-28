package replete.numbers;

public class NumberRange {


    ////////////
    // FIELDS //
    ////////////

    private Number min;      // Non-null, Inclusive
    private Number max;      // Non-null, Inclusive


    //////////////////
    // CONSTRUCTORS //
    //////////////////

    public NumberRange(Number min, Number max) {
        if(min.doubleValue() > max.doubleValue()) {
            throw new IllegalArgumentException("max cannot be less than min");
        }

        this.min = min;
        this.max = max;
    }


    ///////////////
    // ACCESSORS //
    ///////////////

    public Number getMin() {
        return min;
    }
    public Number getMax() {
        return max;
    }


    ////////////////
    // OVERRIDDEN //
    ////////////////

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((max == null) ? 0 : max.hashCode());
        result = prime * result + ((min == null) ? 0 : min.hashCode());
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
        NumberRange other = (NumberRange) obj;
        if(max == null) {
            if(other.max != null) {
                return false;
            }
        } else if(!max.equals(other.max)) {
            return false;
        }
        if(min == null) {
            if(other.min != null) {
                return false;
            }
        } else if(!min.equals(other.min)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        if(min.equals(max)) {
            return min.toString();
        }
        return min + "-" + max;
    }


    //////////
    // MISC //
    //////////

    public boolean isBetween(Number n) {
        return
            n.doubleValue() >= min.doubleValue() &&
            n.doubleValue() <= max.doubleValue();
    }
}
