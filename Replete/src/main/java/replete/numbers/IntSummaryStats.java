package replete.numbers;

import java.io.Serializable;

// Simplified and serializable version of java.util.IntSummaryStatistics
public class IntSummaryStats implements Serializable {


    ////////////
    // FIELDS //
    ////////////

    private long count = 0;
    private long sum   = 0;
    private int  min   = Integer.MAX_VALUE;
    private int  max   = Integer.MIN_VALUE;


    //////////////////
    // CONSTRUCTORS //
    //////////////////

    public IntSummaryStats() {
        // Empty
    }
    private IntSummaryStats(long count, long sum, int min, int max) {    // For copying
        this.count = count;
        this.sum   = sum;
        this.min   = min;
        this.max   = max;
    }


    ////////////////
    // ACCUMULATE //
    ////////////////

    public synchronized void add(int value) {
        count++;
        sum += value;
        min = Math.min(min, value);
        max = Math.max(max, value);
    }
    public synchronized void combine(IntSummaryStats other) {
        count += other.count;
        sum += other.sum;
        min = Math.min(min, other.min);
        max = Math.max(max, other.max);
    }


    ///////////////
    // ACCESSORS //
    ///////////////

    public long getCount() {
        return count;
    }
    public long getSum() {
        return sum;
    }
    public int getMin() {
        return min;
    }
    public int getMax() {
        return max;
    }

    // Computed

    public synchronized double getAverage() {
        return count > 0 ? (double) sum / count : 0.0d;
    }
    public synchronized IntSummaryStats copy() {
        return new IntSummaryStats(
            count, sum, min, max
        );
    }


    //////////
    // MISC //
    //////////

    public String toNiceString() {
        if(count == 0) {
            return "Count: 0";
        }
        return String.format(
            "Count: %d, Sum: %d, Range: [%d, %d], Average=%f",
            count,
            sum,
            min,
            max,
            getAverage()
        );
    }


    ////////////////
    // OVERRIDDEN //
    ////////////////

    @Override
    public synchronized String toString() {
        if(count == 0) {
            return "{count=0}";
        }
        return String.format(
            "{count=%d, sum=%d [%d, %d] avg=%f}",
            count,
            sum,
            min,
            max,
            getAverage()
        );
    }
}
