package replete.profiler;

import replete.util.DateUtil;

public class BlockTimingResult {


    ////////////
    // FIELDS //
    ////////////

    private String name;
    private long start;
    private long previousIterStart;
    private long previousIterEnd;
    private long previousIterElapsed;
    private long totalElapsed;
    private long totalIterations;
    private long estimatedIterations;
    private boolean isStep;
    private boolean isRoot;

    // Computed

    private Long estimatedRemainingIterations;  // All null if estimatedIterations < 0
    private Long estimatedRemainingDuration;
    private Long estimatedEndTime;


    //////////////////
    // CONSTRUCTORS //
    //////////////////

    public BlockTimingResult(String name, long start, long previousIterStart, long previousIterEnd,
                             long previousIterElapsed, long totalElapsed, long totalIterations,
                             long estimatedIterations, long now, boolean isStep, boolean isRoot) {
        this.name = name;
        this.start = start;
        this.previousIterStart = previousIterStart;
        this.previousIterEnd = previousIterEnd;
        this.previousIterElapsed = previousIterElapsed;
        this.totalElapsed = totalElapsed;
        this.totalIterations = totalIterations;
        this.estimatedIterations = estimatedIterations;
        this.isStep = isStep;
        this.isRoot = isRoot;

        if(estimatedIterations >= 0) {
            estimatedRemainingIterations = estimatedIterations - totalIterations;

            double msPerIter = (double) totalElapsed / totalIterations;
            double remaining = (estimatedIterations - totalIterations) * msPerIter;
            estimatedRemainingDuration = (long) remaining;

            estimatedEndTime = now + estimatedRemainingDuration;
        }
    }


    ///////////////
    // ACCESSORS //
    ///////////////

    // Accessors

    public String getName() {
        return name;
    }
    public long getStart() {
        return start;
    }
    public long getPreviousIterStart() {
        return previousIterStart;
    }
    public long getPreviousIterEnd() {
        return previousIterEnd;
    }
    public long getPreviousIterElapsed() {
        return previousIterElapsed;
    }
    public long getTotalElapsed() {          // a.k.a., getBlockDuration()
        return totalElapsed;
    }
    public long getTotalIterations() {
        return totalIterations;
    }
    public long getEstimatedIterations() {
        return estimatedIterations;
    }
    public boolean isStep() {
        return isStep;
    }
    public boolean isRoot() {
        return isRoot;
    }

    // Accessors (Computed)

    public Long getEstimatedRemainingIterations() {
        return estimatedRemainingIterations;
    }
    public Long getEstimatedRemainingDuration() {
        return estimatedRemainingDuration;
    }
    public Long getEstimatedEndTime() {
        return estimatedEndTime;
    }


    ////////////////
    // OVERRIDDEN //
    ////////////////

    public String toString(boolean prettyTime, long baseTime) {
        String est = "";
        String DM = "=";                   // Duration Marker

        if(estimatedIterations >= 0) {
            String erd =
                prettyTime ?
                    DateUtil.toElapsedString(estimatedRemainingDuration) :
                        estimatedRemainingDuration + " ms";
            String eet =
                prettyTime ?
                    DateUtil.toLongString(estimatedEndTime) :
                        (estimatedEndTime - baseTime) + " ms";
            est = " E(" +
                estimatedRemainingIterations + " iter " +
                DM + " " + erd + " remain @ " + eet + " end)";
        }

        // TODO the only reason !sub is here, is because there's
        // no way to add back the class time if pretty wanted....
        String st =
            prettyTime ?
            DateUtil.toLongString(start) :
            (start - baseTime) + " ms";
        String pid =
            prettyTime ?
            DateUtil.toLongString(previousIterStart) + ".." + DateUtil.toLongString(previousIterEnd) :
            (previousIterStart - baseTime) + ".." + (previousIterEnd - baseTime);
        String pie =
            prettyTime ?
            DateUtil.toElapsedString(previousIterElapsed) :
            previousIterElapsed + " ms";
        String te =
            prettyTime ?
            DateUtil.toElapsedString(totalElapsed) :
            totalElapsed + " ms";

        return
            (isRoot ? "^" : "-") +
            " " + name +
            ": S(" + st + ") L(" +
            pid + " " + DM + " " + pie +
            ") T(" + totalIterations +
            " iter " + DM + " " +
            te + ")" + est;
    }
}
