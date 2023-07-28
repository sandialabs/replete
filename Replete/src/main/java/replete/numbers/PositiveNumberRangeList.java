package replete.numbers;

import java.util.ArrayList;
import java.util.List;

import replete.text.StringUtil;

// Designed to be an immutable class, basically meant
// to be parsed statically and then allow for range
// checking of a given number.

// Example: " 1- 4, 12, 100-100, 45  - 48"

// "Positive" just because the - would otherwise get
// confused with negative numbers.  A more enlightened
// syntax and thus parsing would need to be used to
// implement a NumberRangeList.

public class PositiveNumberRangeList {


    ///////////
    // FIELD //
    ///////////

    private List<NumberRange> ranges = new ArrayList<>();


    ///////////
    // PARSE //
    ///////////

    public static PositiveNumberRangeList parse(String str) {
        PositiveNumberRangeList list = new PositiveNumberRangeList();
        String[] rs = str.split("\\s*,\\s*");
        for(String r : rs) {
            String[] ns = r.split("\\s*-\\s*");
            Number min, max;
            if(NumUtil.isInt(ns[0])) {
                min = NumUtil.i(ns[0]);
            } else if(NumUtil.isDouble(ns[0])) {
                min = NumUtil.d(ns[0]);
            } else {
                throw new IllegalArgumentException("number range list parse exception (invalid: " + ns[0] + ")");
            }
            if(ns.length > 1) {
                if(NumUtil.isInt(ns[1])) {
                    max = NumUtil.i(ns[1]);
                } else if(NumUtil.isDouble(ns[1])) {
                    max = NumUtil.d(ns[1]);
                } else {
                    throw new IllegalArgumentException("number range list parse exception (invalid: " + ns[1] + ")");
                }
            } else {
                max = min;
            }
            list.ranges.add(new NumberRange(min, max));
        }
        return list;
    }


    ///////////////
    // ACCESSORS //
    ///////////////

    // Computed

    public boolean contains(Number n) {
        for(NumberRange range : ranges) {
            if(range.isBetween(n)) {
                return true;
            }
        }
        return false;
    }


    ////////////////
    // OVERRIDDEN //
    ////////////////

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((ranges == null) ? 0 : ranges.hashCode());
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
        PositiveNumberRangeList other = (PositiveNumberRangeList) obj;
        if(ranges == null) {
            if(other.ranges != null) {
                return false;
            }
        } else if(!ranges.equals(other.ranges)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return StringUtil.join(ranges, ",");
    }


    //////////
    // TEST //
    //////////

    public static void main(String[] args) {
        PositiveNumberRangeList list = PositiveNumberRangeList.parse(" 1 -8, 19-20");
        for(int i = -1; i < 20; i++) {
            System.out.println(i + ": " + list.contains(i));
        }
    }
}
