package replete.profiler;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import replete.collections.ArrayUtil;
import replete.text.StringUtil;

// TODO: what about endStepAndPrint() ?
// TODO: switch over to nanoTime!
// TODO: a feature to enhance estimated time would be a window of how many iterations or time
//       to consider past iterations' durations.
// TODO: is all the synchronization necessary since
//       all of the actual timing methods should ONLY be called
//       on the same thread?  Each RProfiler instance should
//       hold a reference to the Thread object it corresponds
//       to.
// TODO: static profilers get() method flawed with sync issues
//       just using a CHM doesn't fix the threading problems.
public class RProfiler {


    /////////////////////
    // MULTI-SINGLETON //
    /////////////////////

    private static Map<Thread, RProfiler> profilers = new ConcurrentHashMap<>();
    public static RProfiler get() {
        return get(null);
    }
    public static RProfiler get(String name) {
        Thread cur = Thread.currentThread();
        RProfiler p = profilers.get(cur);
        if(p == null) {
            p = new RProfiler(name, cur);
            profilers.put(cur, p);
        }
        return p;
    }


    ////////////////////
    // INITIALIZATION //
    ////////////////////

    private static long classLoadBaseTime = -1;
    static {
        classLoadBaseTime = System.currentTimeMillis();
    }


    ////////////
    // FIELDS //
    ////////////

    // Constants

    public static final String DEFAULT_NAME = "Profiler";
    public static final boolean DEFAULT_SUBTRACT_BASE_TIME = false;
    private static final String ROOT_NAME = "<ROOT>";
    public static final long NO_EST_ITER = -1;

    // Configuration

    private String name;                                           // Saved for initial block opening
    private boolean subtractBaseTime = DEFAULT_SUBTRACT_BASE_TIME; // Changeable after construction
    private boolean prettyTime;                                    // Changeable after construction
    private long baseTime = classLoadBaseTime;

    // Block Tree State

    private List<Block> stack = new ArrayList<>();
    private Block rootBlock;
    private boolean initialized = false;         // Has had at least one element in the stack before
    private boolean closed = false;
    private Thread thread;


    //////////////////
    // CONSTRUCTORS //
    //////////////////

    private RProfiler(String name, Thread thread) {
        if(name == null) {
            name = DEFAULT_NAME;
        }
        this.name = name;
        this.thread = thread;
    }


    ///////////
    // BLOCK //
    ///////////

    // Allows the creation of a new block.  This can happen at any time,
    // on any thread, except if the profiler is closed.

    // Public
    public BlockTimingResult block(String name) {
        return block(now(), name, NO_EST_ITER);
    }
    public BlockTimingResult block(String name, long estIter) {
        return block(now(), name, estIter);
    }

    // Private
    private synchronized BlockTimingResult block(long now, String name, long estIter) {
        BlockTimingResult endResult = null;
        Block curBlock = getCurrentBlockForThread(now, false, true, "start block");
        if(curBlock.isStep) {       // Steps cannot nest so automatically close the step block.
            endResult = endBlock(now);
        }
        startBlock(now, name, estIter, false);
        return endResult;
    }


    /////////
    // END //
    /////////

    // Convenience method I'm consciously not enabling to bring
    // clarity to and understanding of the RProfiler's API usage.
    // public BlockTimingResult endAndBlock(String name) {
    //     end();
    //     block(name);
    // }

    // Public
    public BlockTimingResult end() {
        return end(now(), null);
    }
    public BlockTimingResult end(String name) {
        return end(now(), name);
    }

    // Private
    private synchronized BlockTimingResult end(long now, String name) {
        if(name != null) {
            if(!hasBlock(name)) {
                throw new RProfilerException("Block name '" + name + "' not found.");
            } else if(name.equals(rootBlock.name)) {
                throw new RProfilerException("Cannot end root block.  Did you mean to close the profiler?");
            }
        }

        Block curBlock = getCurrentBlockForThread(now, false, false, "end block");

        if(curBlock.isStep) {
            if(peekSecondBlock().isRoot) {
                throw new RProfilerException("No block enclosing the step to end.  Did you mean to close the profiler?");
            }
            endBlock(now);      // Steps are closed when their parent block closes.
        } else {
            if(curBlock.isRoot) {
                throw new RProfilerException("No block to end.  Did you mean to close the profiler?");
            }
        }

        BlockTimingResult endResult;
        if(name != null) {
            String curName;
            do {
                curName = peekBlock().name;
                endResult = endBlock(now);
            } while(!curName.equals(name));
        } else {
            endResult = endBlock(now);
        }

        return endResult;
    }


    /////////////////
    // END & PRINT //
    /////////////////

    // Public
    public BlockTimingResult endAndPrint() {
        return endAndPrint(now(), false, -1);
    }
    public BlockTimingResult endAndPrint(boolean printTree) {
        return endAndPrint(now(), printTree, -1);
    }
    public BlockTimingResult endAndPrint(int minimumPrintInterval) {
        return endAndPrint(now(), false, minimumPrintInterval);
    }
    public BlockTimingResult endAndPrint(boolean printTree, int minimumPrintInterval) {
        return endAndPrint(now(), printTree, minimumPrintInterval);
    }

    // Private
    private synchronized BlockTimingResult endAndPrint(long now, boolean printTree, int minimumPrintInterval) {
        Block block = getCurrentBlockForThread(now, true, false, "end and print block");

        BlockTimingResult result = end(now, null);
        long n = result.getPreviousIterEnd();

        // If this block has never been printed or enough time has past since
        // the last printing...
        if(block.lastPrintTime == -1 || n - block.lastPrintTime >= minimumPrintInterval) {
            if(printTree) {
                printTreeTable(block, now, true);
            } else {
                long subtractTime = subtractBaseTime ? baseTime : 0;
                String ts = result.toString(prettyTime, subtractTime);
                System.out.println(ts);
            }
            block.lastPrintTime = n;
        }

        return result;
    }


    //////////
    // STEP //
    //////////

    // Public
    public BlockTimingResult step(String stepName) {
        return step(now(), stepName, NO_EST_ITER);
    }
    public BlockTimingResult step(String stepName, long estIter) {
        return step(now(), stepName, estIter);
    }

    // Private
    private synchronized BlockTimingResult step(long now, String stepName, long estIter) {
        BlockTimingResult endResult = null;
        Block curBlock = getCurrentBlockForThread(now, false, true, "begin step");
        if(curBlock.isStep) {           // Steps cannot nest, so close block if is a step
            endResult = endBlock(now);
        }
        startBlock(now, stepName, estIter, true);
        return endResult;
    }


    //////////////
    // END STEP //
    //////////////

    // Public
    public BlockTimingResult endStep() {
        return endStep(now());
    }

    // Private
    private synchronized BlockTimingResult endStep(long now) {
        Block curBlock = getCurrentBlockForThread(now, false, false, "end step");
        if(!curBlock.isStep) {
            throw new RProfilerException("There is no open step to end.");
        }
        BlockTimingResult endResult = endBlock(now);
        return endResult;
    }


    ///////////
    // CLOSE //
    ///////////

    // Need to implement how block/end/step/endStep fail if closed, but can still print and get other timing info.
    public BlockTimingResult close() {
        return close(now());
    }
    private synchronized BlockTimingResult close(long now) {
        if(closed) {
            return null;
        }
        BlockTimingResult endResult = null;
        while(peekBlock() != null) {
            endResult = endBlock(now);
        }
        closed = true;
        profilers.remove(Thread.currentThread());
        return endResult;
    }


    ///////////////
    // GET BLOCK //
    ///////////////

    Block getCurrentBlockForThread(long now, boolean getBlockIfStep, boolean allowEmpty, String op) {
        if(isEmpty()) {
            if(!allowEmpty) {
                String adj = initialized ? "a closed" : "an unopened";
                throw new RProfilerException("Cannot perform '" + op + "' operation on " + adj + " profiler.");
            }
            if(initialized) {
                throw new RProfilerException("Profiler has been closed");
            }

            rootBlock = new Block(ROOT_NAME, now, NO_EST_ITER, false, true);
            rootBlock.prevStart = now;
            pushBlock(rootBlock);
            initialized = true;
        }

        Block curBlock = peekBlock();
        if(getBlockIfStep && curBlock.isStep) {
            curBlock = peekSecondBlock();
        }
        return curBlock;
    }


    ///////////////////////////
    // PRIVATE BLOCK SUPPORT //
    ///////////////////////////

    // Handles both blocks and steps, which are practically the same
    // thing with the only differences coming in how the developer
    // uses them conceptually.

    private void startBlock(long startTime, String blockName, long estIter, boolean isStep) {
        checkThread();

        // Requiring names for all steps & blocks prevents
        // complicated logic for inventing default names for
        // them (e.g. "Step 7").  You have to know how to choose
        // the name for a child block in both the case where the
        // parent block has never been executed before and in the
        // case where it has been executed at least once.
        if(blockName == null) {
            throw new RProfilerException("Cannot have a null " + (isStep ? "step" : "block") + " name.");
        }

        Block parent = peekBlock();
        Block block = parent.children.get(blockName);
        if(block == null) {
            block = new Block(blockName, startTime, estIter, isStep, false);
            parent.children.put(blockName, block);
        } else {
            block.iterations++;
        }

        // Now that the next block has been chosen, set it's start time
        // and and it to the top of the active block stack.
        block.prevStart = startTime;
        pushBlock(block);
    }

    private BlockTimingResult endBlock(long endTime) {
        checkThread();
        Block block = popBlock();
        long blockLastIterEnd = endTime;
        long blockLastIterElapsed = blockLastIterEnd - block.prevStart;
        block.registerElapsed(blockLastIterEnd, blockLastIterElapsed);

        return new BlockTimingResult(
            block.name,       block.start,               block.prevStart,
            block.prevEnd,    block.prevElapsed,         block.totalElapsed,
            block.iterations, block.estimatedIterations, block.prevEnd,
            block.isStep,     block.isRoot
        );
    }

    private void checkThread() {
        if(Thread.currentThread() != thread) {
            throw new RProfilerException("Cannot execute profiling commands for an RProfiler from a thread different than the one it is made for.");
        }
    }


    ///////////
    // PRINT //
    ///////////

    public void printAll() {
        printAll(true);
    }
    public void printAll(boolean appendProfilerId) {
        printTreeTable(rootBlock, now(), appendProfilerId);
    }
    public String getTreeString() {
        return getTreeString(true);
    }
    public String getTreeString(boolean appendProfilerId) {
        return generateTreeTable(rootBlock, now(), appendProfilerId);
    }

    // Helpers

    private void printTreeTable(Block block, long now, boolean appendProfilerId) {
        String str = generateTreeTable(block, now, appendProfilerId);
        System.out.print(str);
    }
    private synchronized String generateTreeTable(Block block, long now, boolean appendProfilerId) {
        // TODO: look into performance of this method
        List<String[]> rows = new ArrayList<>();
        if(block != null) {      // Can be null for uninitialized RProfiler.
            findTableRows(block, 0, rows, now, 0);        // Recursive
        }

        int permanentColumns = 5;
        int possibleColumns = 8;
        String[] headers = new String[] {"Block", "Time", "%", "Iter", "Avg", "E*Iter", "E*Dur", "E*End"};
        int[] maxLengths = new int[possibleColumns];
        rows.add(0, headers);

        boolean est = false;
        for(String[] row : rows) {
            for(int c = 0; c < row.length; c++) {
                int curLength = row[c].length();
                if(curLength > maxLengths[c]) {
                    maxLengths[c] = curLength;
                }
                if(row != headers && c >= permanentColumns && curLength != 0) {
                    est = true;
                }
            }
        }

        int cols = est ? possibleColumns : permanentColumns;  // With or without estimated columns
        rows.remove(0);

        // Create the printf code.
        StringBuilder codeBuffer = new StringBuilder();
        for(int c = 0; c < cols; c++) {
            codeBuffer.append((c == 0) ? "%-" : "%");
            codeBuffer.append(maxLengths[c]);
            codeBuffer.append("s  ");
        }
        String code = codeBuffer.toString().trim();

        // Append header information.
        StringBuilder b = new StringBuilder();
        if(appendProfilerId) {
            b.append("<Profiler \"" + name + "\" on thread \"" + thread + "\">\n");
        }
        String hdrString = String.format(code, ArrayUtil.subset(headers, 0, cols));
        String dashes    = StringUtil.replicateChar('-', hdrString.length());
        b.append(hdrString); b.append("\n");
        b.append(dashes);    b.append("\n");

        if(rows.isEmpty()) {
            b.append("(No Blocks Initialized)");
        } else {
            for(String[] row : rows) {
                b.append(String.format(code, ArrayUtil.subset(row, 0, cols)));
                b.append('\n');
            }
        }
        return b.toString();
    }

    private void findTableRows(Block block, int level, List<String[]> rows, long now, long oldestAncestorElapsed) {
        String div = block.isRoot ? "^ " : "- ";
        if(oldestAncestorElapsed == 0 && block.atLeastOneElapsed) {
            oldestAncestorElapsed = block.totalElapsed;
        }
        String[] fields = block.getPrintFields(prettyTime, subtractBaseTime ? baseTime : 0, now, oldestAncestorElapsed);
        fields[0] = StringUtil.spaces(level * 2) + div + fields[0];
        rows.add(fields);
        for(String label : block.children.keySet()) {
            Block child = block.children.get(label);
            findTableRows(child, level + 1, rows, now, oldestAncestorElapsed);
        }
    }

// Probably not needed...
//    public long getCurrentIterStart() {
//        long now = now();
//        Block block = getCurrentBlockForThread(now, false, true, "get current iter start");
//        return block.prevStart;
//    }
//    public long peek() {
//        long now = now();
//        Block block = getCurrentBlockForThread(now, false, true, "peek");
//        long elapsed = now - block.prevStart;
//        return elapsed;
//    }


    //////////////////////////
    // ACCESSORS / MUTATORS //
    //////////////////////////

    // Accessors (Static)

    public static long getClassLoadBaseTime() {
        return classLoadBaseTime;
    }

    // Accessors (Static, Computed)

    public static long now() {
        return System.currentTimeMillis();       // Convenience
    }

    // Accessors

    public String getName() {
        return name;
    }
    public long getBaseTime() {
        return baseTime;
    }
    public boolean isSubtractBaseTime() {
        return subtractBaseTime;
    }
    public boolean isPrettyTime() {
        return prettyTime;
    }
    public Block getRootBlock() {
        return rootBlock;
    }
    public boolean isInitialized() {
        return initialized;
    }
    public boolean isClosed() {
        return closed;
    }

    // Accessors (Computed)

    public int getNodeCount() {
        if(rootBlock == null) {
            return 0;
        }
        return rootBlock.getNodeCount();
    }

    // Accessors (Computed) - Stack

    Block peekBlock() {
        return stack.isEmpty() ? null : stack.get(stack.size() - 1);
    }
    Block peekSecondBlock() {
        int sz = stack.size();
        return sz < 2 ? null : stack.get(sz - 2);   // Only called when there's at least 2 elements in stack
    }
    private void pushBlock(Block block) {
        stack.add(block);
    }
    Block popBlock() {
        return stack.isEmpty() ? null : stack.remove(stack.size() - 1);
    }
    List<Block> getStack() {
        return stack;
    }
    public boolean isEmpty() {
        return stack.isEmpty();
    }
    private boolean hasBlock(String name) {
        boolean found = false;
        int s = stack.size() - 1;
        while(s >= 0 && !(found = stack.get(s).name.equals(name))) {
            s--;
        }
        return found;
    }

    // Mutators

    public RProfiler setName(String name) {
        this.name = name;
        return this;
    }
    public RProfiler setBaseTime(long baseTime) {
        this.baseTime = baseTime;
        return this;
    }
    public RProfiler setSubtractBaseTime(boolean subtractBaseTime) {
        this.subtractBaseTime = subtractBaseTime;
        return this;
    }
    public RProfiler setPrettyTime(boolean prettyTime) {
        this.prettyTime = prettyTime;
        return this;
    }


    //////////
    // TEST //
    //////////

    public static void main(String[] args) {
        RProfiler P = get();
        P.close();
        System.out.println(P.getTreeString());

//        testMilliTime();
//        testNanoTime();
    }

//    private static void testMilliTime() {
//        long Tb = System.currentTimeMillis();
//        long Tp = Tb;
//        int c = 0;
//        int d = 0;
//        while(Tp - Tb < 10000) {
//            c++;
//            long Tn = System.currentTimeMillis();
//            long del = Tn - Tp;
//            if(del != 0) {
//                System.out.println("Changed: WAS=" + Tp + ", NOW=" + Tn + ", DEL=" + del);
//                d++;
//            }
//            Tp = Tn;
//        }
//        System.out.println("C=" + c + ", D=" + d);
//        System.out.println(10000.0 / d);
//    }
//
//    private static void testNanoTime() {
//        long Tb = System.nanoTime();
//        long Tp = Tb;
//        int c = 0;
//        int d = 0;
//        while(Tp - Tb < 10000000000L) {
//            c++;
//            long Tn = System.nanoTime();
//            long del = Tn - Tp;
//            if(del != 0) {
//                System.out.println("Changed: WAS=" + Tp + ", NOW=" + Tn + ", DEL=" + del);
//                d++;
//            }
//            Tp = Tn;
//        }
//        System.out.println("C=" + c + ", D=" + d);
//        System.out.println(10000.0 / d);
//    }
}
