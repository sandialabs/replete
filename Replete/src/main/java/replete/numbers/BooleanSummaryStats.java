package replete.numbers;

import java.io.Serializable;

public class BooleanSummaryStats implements Serializable {


    ////////////
    // FIELDS //
    ////////////

    private long trueCount  = 0;
    private long falseCount = 0;


    //////////////////
    // CONSTRUCTORS //
    //////////////////

    public BooleanSummaryStats() {
        // Empty
    }
    private BooleanSummaryStats(long trueCount, long falseCount) {    // For copying
        this.trueCount = trueCount;
        this.falseCount = falseCount;
    }


    ////////////////
    // ACCUMULATE //
    ////////////////

    public synchronized void add(boolean value) {
        if(value) {
            trueCount++;
        } else {
            falseCount++;
        }
    }
    public synchronized void combine(BooleanSummaryStats other) {
        trueCount  += other.trueCount;
        falseCount += other.falseCount;
    }


    ///////////////
    // ACCESSORS //
    ///////////////

    public long getTrueCount() {
        return trueCount;
    }
    public long getFalseCount() {
        return falseCount;
    }

    // Computed

    public synchronized long getCount() {
        return trueCount + falseCount;
    }
    public synchronized double getTruePercent() {
        long totalCount = getCount();
        return totalCount > 0 ? (double) trueCount / totalCount : 0.0d;
    }
    public synchronized BooleanSummaryStats copy() {
        return new BooleanSummaryStats(
            trueCount, falseCount
        );
    }


    ////////////////
    // OVERRIDDEN //
    ////////////////

    @Override
    public synchronized String toString() {
        if(getCount() == 0) {
            return String.format(
                "{count=0}"
            );
        }
        return String.format(
            "{count=%d, true=%d, false=%d, true%%=%s}",
            getCount(),
            trueCount,
            falseCount,
            NumUtil.pct(getTruePercent())
        );
    }
}
