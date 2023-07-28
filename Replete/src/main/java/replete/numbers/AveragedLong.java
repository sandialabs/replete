package replete.numbers;

import java.io.Serializable;

public class AveragedLong implements Serializable {


    ////////////
    // FIELDS //
    ////////////

    private long last;
    private long total;
    private int count;


    //////////////////
    // CONSTRUCTORS //
    //////////////////

    public AveragedLong() {
        // Nothing
    }
    // Copy constructor
    public AveragedLong(AveragedLong other) {
        synchronized(other) {
            last = other.last;
            total = other.total;
            count = other.count;
        }
    }


    //////////////////////////
    // ACCESSORS / MUTATORS //
    //////////////////////////

    // Accessors

    public long getLast() {
        return last;
    }
    public long getTotal() {
        return total;
    }
    public int getCount() {
        return count;
    }

    // Accessor (Computed)

    public synchronized double getAverage() {   // synchronized might be unneccessary here
        return (double) total / count;
    }

    // Mutators

    public synchronized void setAndIncrement(long cur) {
        last = cur;
        total += cur;
        count++;
    }
    public synchronized void setLast(long newLast) {
        last = newLast;
    }
    @Override
    public String toString() {
        return "{L" + last + " T" + total + " C" + count + "}";
    }
}
