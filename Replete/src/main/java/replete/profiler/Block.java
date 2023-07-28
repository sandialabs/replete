package replete.profiler;

import java.util.LinkedHashMap;
import java.util.Map;

import replete.util.DateUtil;

public class Block {


    ////////////
    // FIELDS //
    ////////////

    protected String name;
    protected boolean isStep;
    protected boolean isRoot;
    protected long start;
    protected long prevStart;
    protected long prevEnd;
    protected long minElapsed = Long.MAX_VALUE;
    protected long maxElapsed = Long.MIN_VALUE;
    protected long prevElapsed;
    protected long totalElapsed;
    protected int iterations;
    protected long estimatedIterations;
    protected long lastPrintTime = -1;
    protected boolean atLeastOneElapsed = false;
    protected Map<String, Block> children = new LinkedHashMap<>();


    //////////////////
    // CONSTRUCTORS //
    //////////////////

    public Block(String name, long start, long estimatedIterations, boolean isStep, boolean isRoot) {
        this.name = name;
        this.start = start;
        iterations = 1;
        this.estimatedIterations = estimatedIterations;
        this.isStep = isStep;
        this.isRoot = isRoot;
    }


    //////////////////////////
    // ACCESSORS / MUTATORS //
    //////////////////////////

    // Accessor (Computed)

    public int getNodeCount() {
        int count = 1;
        for(Block block : children.values()) {
            count += block.getNodeCount();
        }
        return count;
    }

    // Mutator

    public void registerElapsed(long newEnd, long newElapsed) {
        prevEnd = newEnd;
        prevElapsed = newElapsed;
        totalElapsed += newElapsed;
        if(newElapsed < minElapsed) {
            minElapsed = newElapsed;
        }
        if(newElapsed > maxElapsed) {
            maxElapsed = newElapsed;
        }
        atLeastOneElapsed = true;
    }


    ////////////
    // RENDER //
    ////////////

    public String[] getPrintFields(boolean prettyTime, long baseTime, long now, long oldestAncestorElapsed) {
        double avg = (double) totalElapsed / iterations;
        avg = Math.round(avg * 10) / 10.0;
        String e, a;
        String estIter = "";
        String estRem = "";
        String estTime = "";

        if(!atLeastOneElapsed) {
            e = "*";
            a = "*";
        } else {
            e = prettyTime ? DateUtil.toElapsedString(totalElapsed) : totalElapsed + " ms";
            a = prettyTime ? DateUtil.toElapsedString((long) avg) : avg + " ms";
        }

        String pct;
        if(oldestAncestorElapsed > 0L) {
            double p = (double) totalElapsed / oldestAncestorElapsed * 100;
            pct = String.format("%.1f%%", p);
        } else {
            pct = "*";
        }

        if(estimatedIterations >= 0) {
            long estimatedRemainingIterations = estimatedIterations - iterations;

            double msPerIter = (double) totalElapsed / iterations;
            double remaining = (estimatedIterations - iterations) * msPerIter;
            long estimatedRemainingDuration = (long) remaining;

            long estimatedEndTime = now + estimatedRemainingDuration;

            estIter = "" + estimatedRemainingIterations;

            estRem =
                prettyTime ?
                    DateUtil.toElapsedString(estimatedRemainingDuration) :
                        estimatedRemainingDuration + " ms";
            estTime =
                prettyTime ?
                    DateUtil.toLongString(estimatedEndTime) :
                        (estimatedEndTime - baseTime) + " ms";
        }

        return new String[] {
            name,
            "" + e,
            pct,
            "" + iterations,
            "" + a,
            estIter,
            estRem,
            estTime
        };
    }


    ////////////////
    // OVERRIDDEN //
    ////////////////

    @Override
    public String toString() {
        return name + "(" +
            (isRoot?"root":"") +
            (isStep?"step":"") +
            (!isRoot && !isStep?"normal":"") +
            ",it=" + iterations +
            ",te=" + totalElapsed +
        ")";
    }
}
