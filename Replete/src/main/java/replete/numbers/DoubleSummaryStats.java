package replete.numbers;

import java.io.Serializable;

// Simplified and serializable version of java.util.DoubleSummaryStatistics
public class DoubleSummaryStats implements Serializable {


    ////////////
    // FIELDS //
    ////////////

    private long   count           = 0;
    private double sum             = 0.0;
    private double sumCompensation = 0.0;       // Low order bits of sum
    private double simpleSum       = 0.0;       // Used to compute right sum for non-finite inputs
    private double min             = Double.POSITIVE_INFINITY;
    private double max             = Double.NEGATIVE_INFINITY;


    //////////////////
    // CONSTRUCTORS //
    //////////////////

    public DoubleSummaryStats() {
        // Empty
    }
    private DoubleSummaryStats(long count, double sum, double sumCompensation,
                               double simpleSum, double min, double max) {     // For copying
        this.count           = count;
        this.sum             = sum;
        this.sumCompensation = sumCompensation;
        this.simpleSum       = simpleSum;
        this.min             = min;
        this.max             = max;
    }


    ////////////////
    // ACCUMULATE //
    ////////////////

    public synchronized void add(double value) {
        ++count;
        simpleSum += value;
        sumWithCompensation(value);
        min = Math.min(min, value);
        max = Math.max(max, value);
    }
    public synchronized void combine(DoubleSummaryStats other) {
        count += other.count;
        simpleSum += other.simpleSum;
        sumWithCompensation(other.sum);
        sumWithCompensation(other.sumCompensation);
        min = Math.min(min, other.min);
        max = Math.max(max, other.max);
    }

    private void sumWithCompensation(double value) {
        double tmp = value - sumCompensation;
        double velvel = sum + tmp; // Little wolf of rounding error
        sumCompensation = (velvel - sum) - tmp;
        sum = velvel;
    }


    ///////////////
    // ACCESSORS //
    ///////////////

    public long getCount() {
        return count;
    }
    public double getMin() {
        return min;
    }
    public double getMax() {
        return max;
    }

    // Computed

    public synchronized double getSum() {
        // Better error bounds to add both terms as the final sum
        double tmp =  sum + sumCompensation;
        if(Double.isNaN(tmp) && Double.isInfinite(simpleSum)) {
            // If the compensated sum is spuriously NaN from
            // accumulating one or more same-signed infinite values,
            // return the correctly-signed infinity stored in
            // simpleSum.
            return simpleSum;
        }
        return tmp;
    }
    public synchronized double getAverage() {
        return count > 0 ? getSum() / count : 0.0d;
    }
    public synchronized DoubleSummaryStats copy() {
        return new DoubleSummaryStats(
            count, sum, sumCompensation, simpleSum, min, max
        );
    }


    ////////////////
    // OVERRIDDEN //
    ////////////////

    @Override
    public synchronized String toString() {
        if(count == 0) {
            return String.format(
                "{count=0}"
            );
        }
        return String.format(
            "{count=%d, sum=%f [%f, %f] avg=%f}",
            count,
            getSum(),
            min,
            max,
            getAverage()
        );
    }
}
