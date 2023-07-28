package replete.profiler;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.junit.Ignore;
import org.junit.Test;

import replete.threads.ThreadUtil;

@Ignore("Tests currently have too much variability from host to host.")
public class RProfilerTest {

    private static final boolean print = true;

    @Test
    public void construction() {
        RProfiler P = RProfiler.get();
        check(P, RProfiler.DEFAULT_NAME, RProfiler.DEFAULT_SUBTRACT_BASE_TIME);
        P.close();

        P = RProfiler.get(null);
        check(P, RProfiler.DEFAULT_NAME, RProfiler.DEFAULT_SUBTRACT_BASE_TIME);
        P.close();

        P = RProfiler.get("Tony");
        check(P, "Tony", RProfiler.DEFAULT_SUBTRACT_BASE_TIME);
        P.close();

        P = RProfiler.get("Mark").setSubtractBaseTime(true);
        check(P, "Mark", true);
        P.close();

        P = RProfiler.get().setSubtractBaseTime(true);       // Same name as above, boolean ignored
        check(P, RProfiler.DEFAULT_NAME, true);

        assertFalse(P.isPrettyTime());
        assertEquals(P.getBaseTime(), RProfiler.getClassLoadBaseTime());
        assertNull(P.getRootBlock());   // Should not be any root blocks.
        assertFalse(P.isInitialized());
        assertFalse(P.isClosed());
        assertTrue(P.isEmpty());
        assertNull(P.peekBlock());
        assertNull(P.peekSecondBlock());
        assertNull(P.popBlock());

        P.close();
    }

    @Test
    public void endBeforeOpen() {
        RProfiler P = RProfiler.get("endBeforeOpen");

        try {
            P.end();
            fail();
        } catch(Exception e) {
            assertEquals("Cannot perform 'end block' operation on an unopened profiler.", e.getMessage());
        }
        try {
            P.endAndPrint();
            fail();
        } catch(Exception e) {
            assertEquals("Cannot perform 'end and print block' operation on an unopened profiler.", e.getMessage());
        }
        try {
            P.endAndPrint(false);
            fail();
        } catch(Exception e) {
            assertEquals("Cannot perform 'end and print block' operation on an unopened profiler.", e.getMessage());
        }
        try {
            P.endAndPrint(0);
            fail();
        } catch(Exception e) {
            assertEquals("Cannot perform 'end and print block' operation on an unopened profiler.", e.getMessage());
        }
        try {
            P.endAndPrint(false, 0);
            fail();
        } catch(Exception e) {
            assertEquals("Cannot perform 'end and print block' operation on an unopened profiler.", e.getMessage());
        }
        try {
            P.endStep();
            fail();
        } catch(Exception e) {
            assertEquals("Cannot perform 'end step' operation on an unopened profiler.", e.getMessage());
        }

        P.close();
    }

    @Test
    public void firstNullBlocks() {
        RProfiler P = RProfiler.get("firstNullBlocks");
        try {
            P.block(null);
            fail();
        } catch(Exception e) {
            assertEquals("Cannot have a null block name.", e.getMessage());
        }
        try {
            P.block(null, 0);
            fail();
        } catch(Exception e) {
            assertEquals("Cannot have a null block name.", e.getMessage());
        }
        try {
            P.step(null);
            fail();
        } catch(Exception e) {
            assertEquals("Cannot have a null step name.", e.getMessage());
        }
        try {
            P.step(null, 0);
            fail();
        } catch(Exception e) {
            assertEquals("Cannot have a null step name.", e.getMessage());
        }

        P.close();
        if(print) {
            P.printAll();
        }
    }

    @Test
    public void firstBlocks() {
        RProfiler P = RProfiler.get("firstBlocks");
        check(P, "firstBlocks", 0, false, false, true, true);

        BlockTimingResult R = P.block("A");
        assertNull(R);

        check(P, "firstBlocks", 2, true, false, false, false);

        Block B = P.getStack().get(1);
        check(B, "A", false, false);

        try {
            P.endStep();
        } catch(Exception e) {
            assertEquals("There is no open step to end.", e.getMessage());
        }

        R = P.end();
        check(R, "A");

        assertEquals(B.start, R.getStart());

        check(P, "firstBlocks", 1, true, false, false, true);

        try {
            P.endStep();
        } catch(Exception e) {
            assertEquals("There is no open step to end.", e.getMessage());
        }

        try {
            P.end();
        } catch(Exception e) {
            assertEquals("No block to end.  Did you mean to close the profiler?", e.getMessage());
        }

        R = P.step("S");
        assertNull(R);

        B = P.getStack().get(1);
        check(B, "S", true, false);
        B = P.getStack().get(0);
        check(B, "<ROOT>", false, true);

        try {
            P.end();
        } catch(Exception e) {
            assertEquals("No block enclosing the step to end.  Did you mean to close the profiler?", e.getMessage());
        }

        R = P.endStep();
        check(R, "S");

        check(P, "firstBlocks", 1, true, false, false, true);

        R = P.close();
        check(P, "firstBlocks", 0, true, true, true, true);
        check(R, "<ROOT>");

        P.close();     // OK to call again, no-op
        if(print) {
            P.printAll();
        }

        // Need to implement how block/end/step/endStep fail if closed,
        // but can still print and get other timing info
    }

    @Test
    public void basicStats() {
        int sleep = 500;
        RProfiler P = RProfiler.get("basicStats");
//        P.setPrettyTime(true);

        P.block("A");
        check(P, "basicStats", 2, true, false, false, false);
        sl(sleep);
        BlockTimingResult R = P.end();
//        System.out.println(R.toString(P.isPrettyTime(), 0));

        check(P, "basicStats", 1, true, false, false, true);
        long elapsedMin = R.getPreviousIterElapsed();

        long start = R.getStart();
        assertEquals(R.getStart(), R.getPreviousIterStart());
        assertEquals(R.getPreviousIterElapsed(), R.getPreviousIterEnd() - R.getPreviousIterStart());
        assertTrue(Math.abs(R.getPreviousIterElapsed() - sleep) < 10);     // +-10ms
        assertEquals(R.getTotalElapsed(), R.getPreviousIterElapsed());
        assertEquals(1, R.getTotalIterations());
        assertEquals(-1, R.getEstimatedIterations());
        assertNull(R.getEstimatedRemainingIterations());
        assertNull(R.getEstimatedRemainingDuration());
        assertNull(R.getEstimatedEndTime());

        P.block("A");
        sl(sleep + 20);
        R = P.end();
//        System.out.println(R.toString(P.isPrettyTime(), 0));

        assertEquals(start, R.getStart());
        assertEquals(R.getPreviousIterElapsed(), R.getPreviousIterEnd() - R.getPreviousIterStart());
        assertTrue(Math.abs(R.getPreviousIterElapsed() - (sleep + 20)) < 10);     // +-10ms
        assertTrue(Math.abs(R.getTotalElapsed() - (sleep + sleep + 20)) < 10);    // +-10ms
        assertEquals(2, R.getTotalIterations());
        assertEquals(-1, R.getEstimatedIterations());

        P.block("A");
        check(P, "basicStats", 2, true, false, false, false);
        sl(sleep + 40);
        R = P.end();
//        System.out.println(R.toString(P.isPrettyTime(), 0));

        check(P, "basicStats", 1, true, false, false, true);
        long prevStart = R.getPreviousIterStart();
        long prevEnd = R.getPreviousIterEnd();
        long prevElapsed = R.getPreviousIterElapsed();
        long elapsedMax = R.getPreviousIterElapsed();
        long totalElapsed = R.getTotalElapsed();

        assertEquals(start, R.getStart());
        assertEquals(R.getPreviousIterElapsed(), R.getPreviousIterEnd() - R.getPreviousIterStart());
        assertTrue(Math.abs(R.getPreviousIterElapsed() - (sleep + 40)) < 10);     // +-10ms
        assertTrue(Math.abs(R.getTotalElapsed() - (sleep + sleep + sleep + 20 + 40)) < 10);    // +-10ms
        assertEquals(3, R.getTotalIterations());
        assertEquals(-1, R.getEstimatedIterations());

        assertEquals(1, P.getRootBlock().children.size());
        Block B = P.getRootBlock().children.get("A");
        check(B, "A", false, false, start, prevStart, prevEnd,
            prevElapsed, elapsedMin, elapsedMax, totalElapsed,
            3, 0, -1, -1);

        R = P.close();
        if(print) {
            P.printAll();
        }
    }

    @Test
    public void basicStatsWithElapsed() {
        int sleep = 500;

        RProfiler P = RProfiler.get("basicStatsWithElapsed");
        P.setPrettyTime(false);

        P.block("A", 3);
        check(P, "basicStatsWithElapsed", 2, true, false, false, false);
        sl(sleep);
        BlockTimingResult R = P.end();
//        System.out.println(R.toString(P.isPrettyTime(), 0));

        check(P, "basicStatsWithElapsed", 1, true, false, false, true);
        long elapsedMin = R.getPreviousIterElapsed();

        long start = R.getStart();
        assertEquals(R.getStart(), R.getPreviousIterStart());
        assertEquals(R.getPreviousIterElapsed(), R.getPreviousIterEnd() - R.getPreviousIterStart());
        assertTrue(Math.abs(R.getPreviousIterElapsed() - sleep) < 10);     // +-10ms
        assertEquals(R.getTotalElapsed(), R.getPreviousIterElapsed());
        assertEquals(1, R.getTotalIterations());
        assertEquals(3, R.getEstimatedIterations());

        assertEquals(new Long(2), R.getEstimatedRemainingIterations());
        assertTrue(Math.abs(new Long(sleep * 2) - R.getEstimatedRemainingDuration()) < 5);   // +-5ms
//        assertEquals(?, R.getEstimatedEndTime());

        P.block("A");
        sl(sleep + 20);
        R = P.end();
//        System.out.println(R.toString(P.isPrettyTime(), 0));

        assertEquals(start, R.getStart());
        assertEquals(R.getPreviousIterElapsed(), R.getPreviousIterEnd() - R.getPreviousIterStart());
        assertTrue(Math.abs(R.getPreviousIterElapsed() - (sleep + 20)) < 10);     // +-10ms
        assertTrue(Math.abs(R.getTotalElapsed() - (sleep + sleep + 20)) < 10);    // +-10ms
        assertEquals(2, R.getTotalIterations());
        assertEquals(3, R.getEstimatedIterations());

        assertEquals(new Long(1), R.getEstimatedRemainingIterations());
        assertTrue(Math.abs(new Long((sleep + sleep + 20) / 2) - R.getEstimatedRemainingDuration()) < 5);   // +-5ms
//        assertEquals(?, R.getEstimatedEndTime());

        P.block("A");
        check(P, "basicStatsWithElapsed", 2, true, false, false, false);
        sl(sleep + 40);
        R = P.end();
//        System.out.println(R.toString(P.isPrettyTime(), 0));

        check(P, "basicStatsWithElapsed", 1, true, false, false, true);
        long prevStart = R.getPreviousIterStart();
        long prevEnd = R.getPreviousIterEnd();
        long prevElapsed = R.getPreviousIterElapsed();
        long elapsedMax = R.getPreviousIterElapsed();
        long totalElapsed = R.getTotalElapsed();

        assertEquals(start, R.getStart());
        assertEquals(R.getPreviousIterElapsed(), R.getPreviousIterEnd() - R.getPreviousIterStart());
        assertTrue(Math.abs(R.getPreviousIterElapsed() - (sleep + 40)) < 10);     // +-10ms
        assertTrue(Math.abs(R.getTotalElapsed() - (sleep + sleep + sleep + 20 + 40)) < 10);    // +-10ms
        assertEquals(3, R.getTotalIterations());
        assertEquals(3, R.getEstimatedIterations());

        assertEquals(new Long(0), R.getEstimatedRemainingIterations());
        assertTrue(Math.abs(new Long(0) - R.getEstimatedRemainingDuration()) < 5);   // +-5ms
        assertTrue(Math.abs(System.currentTimeMillis() - R.getEstimatedEndTime()) < 3);   // +-5ms

        assertEquals(1, P.getRootBlock().children.size());
        Block B = P.getRootBlock().children.get("A");
        check(B, "A", false, false, start, prevStart, prevEnd,
            prevElapsed, elapsedMin, elapsedMax, totalElapsed,
            3, 0, 3, -1);

        R = P.close();
        if(print) {
            P.printAll();
        }
    }

    @Test
    public void blockCounts() {
        RProfiler P = RProfiler.get();
        check(P, 0, 0);
        P.block("A");
        check(P, 2, 2);
        P.step("A.S1");
        check(P, 3, 3);
        P.step("A.S2");
        check(P, 4, 3);
        P.block("A.A");
        check(P, 5, 3);
        P.block("A.A.A");P.end();
        check(P, 6, 3);
        P.block("A.A.A");P.end();
        check(P, 6, 3);
        P.block("A.A.A");P.end();
        check(P, 6, 3);
        P.step("A.A.S1");
        check(P, 7, 4);
        P.step("A.A.S3");
        check(P, 8, 4);
//        P.endStep();
//        check(P, 8, 3);
        P.end();
        check(P, 8, 2);
        P.block("A.B");
        check(P, 9, 3);
        P.block("A.B.A");
        check(P, 10, 4);
        P.block("A.B.A.A");
        check(P, 11, 5);
        P.block("A.B.A.A.A");
        check(P, 12, 6);
        P.step("A.B.A.A.A.S1");
        check(P, 13, 7);
        P.step("A.B.A.A.A.S2");
        check(P, 14, 7);
        P.end();
        check(P, 14, 5);
        P.end();
        check(P, 14, 4);
        P.end();
        check(P, 14, 3);
        P.end();
        check(P, 14, 2);
        P.end();
        check(P, 14, 1);
        P.close();
        check(P, 14, 0);
        if(print) {
            P.printAll();
        }
    }

    @Test
    public void blockEndWithName() {
        RProfiler P = RProfiler.get();
        check(P, 0, 0);
        P.block("A");
        check(P, 2, 2);
        P.step("A.S1");
        check(P, 3, 3);
        P.step("A.S2");
        check(P, 4, 3);
        P.block("A.A");
        check(P, 5, 3);
        P.step("A.A.S1");
        try {
            P.end("X");
            fail();
        } catch(Exception e) {
            assertEquals("Block name 'X' not found.", e.getMessage());
        }
        P.end("A");
        check(P, 6, 1);
        P.close();
        if(print) {
            P.printAll();
        }
    }

    @Test
    public void single() {
        RProfiler P = RProfiler.get();
        for(int i = 0; i < 3; i++) {
            P.step("one");
            sl(100);
            P.step("two");
            sl(200);
            P.step("three");
            sl(300);
        }
        P.block("mid");
        P.end();
        P.step("last");
        BlockTimingResult R = P.close();
        assertTrue(R.getTotalElapsed() - 1800 < 20);     // +-20ms
        if(print) {
            P.printAll();
        }
    }

//    @Test
//    public void doubleNested() {
//        RProfiler P = RProfiler.get();
//        check(P, 0, 0);
//        P.step("first line");
//        check(P, 2, 2);
//        P.step("second line");
//        check(P, 3, 2);
//        for(int b = 0; b < 10; b++) {
//            P.block("OUTER");
//            check(P, b == 0 ? 4 : 7, 2);
//            for(int c = 0; c < 10; c++) {
//                P.block("INNER");
//                check(P, b == 0 && c == 0 ? 5 : 7, 3);
//                for(int d = 0; d < 10; d++) {
//                    P.block("a");
//                    check(P, b == 0 && c == 0 ? 6 : 7, 4);
//                    sl(1);
//                    P.end();
//                    check(P, b == 0 && c == 0 ? 6 : 7, 3);
//                }
//                P.block("b"); P.end();
//                P.end();
//                check(P, 7, 2);
//            }
//            sl(2);
//            P.end();
//            check(P, 7, 1);
//        }
//        P.block("z");
//        check(P, 8, 2);
//        P.step("z.1");
//        check(P, 9, 3);
//        P.step("z.2");
//        check(P, 10, 3);
//        P.end();
//        check(P, 10, 1);
//        sl(2000);
//        BlockTimingResult R = P.close();
//        check(P, 10, 0);
//        assertTrue(R.getTotalElapsed() - 3020 < 80);     // +-80ms
//        if(print) {
//            P.printAll();
//        }
//    }

    @Test
    public void estimated() {
        BlockTimingResult R = null;
        RProfiler P = RProfiler.get().setSubtractBaseTime(true);
        for(int i = 0; i < 10; i++) {
            P.block("inner", 10);
            P.block("inner2");
            P.step("hi");
            ThreadUtil.sleep(1000);
            P.step("there");
            R = P.endStep();
            assertTrue(R.getTotalElapsed() - 0 < 2);     // +-2ms

            R = P.end();
            assertTrue(R.getTotalElapsed() - 1000 * (i + 1) < (i + 1) * 4);     // +-80ms
            assertNull(R.getEstimatedRemainingIterations());

            R = P.end();
            assertTrue(R.getTotalElapsed() - 1000 * (i + 1) < (i + 1) * 4);     // +-80ms
            assertEquals(10, R.getEstimatedIterations());
            assertEquals(new Long(10 - (i + 1)), R.getEstimatedRemainingIterations());
            assertTrue(R.getEstimatedRemainingDuration() - (10 - (i + 1)) * 1000 < (i + 1) * 20);
        }

        Long end = R.getEstimatedEndTime();
        assertTrue(Math.abs(RProfiler.now() - end) < 5);

        P.close();
        if(print) {
            P.printAll();
        }
    }


    ////////////
    // HELPER //
    ////////////

    private void sl(int ms) {
        ThreadUtil.sleep(ms);
    }

    private void check(RProfiler P, String name, boolean sbt) {
        if(!P.getName().equals(name) || P.isSubtractBaseTime() != sbt) {
            throw new AssertionError("invalid RProfiler");
        }
    }
    private void check(RProfiler P, String name, int stackSize, boolean init, boolean closed, boolean peekNull, boolean peekSecondNull) {
        assertNotNull(P);
        assertEquals(name, P.getName());
        assertEquals(stackSize, P.getStack().size());
        assertTrue(P.isEmpty() == (stackSize == 0));
        assertEquals(init, P.isInitialized());
        assertEquals(closed, P.isClosed());
        if(stackSize > 0) {
            assertSame(P.getRootBlock(), P.getStack().get(0));
        }
        if(init) {
            assertNotNull(P.getRootBlock());
            assertTrue(P.getRootBlock().isRoot);
            assertFalse(P.getRootBlock().isStep);
        } else {
            assertNull(P.getRootBlock());
        }
        if(peekNull) {
            assertNull(P.peekBlock());
        } else {
            assertNotNull(P.peekBlock());
        }
        if(peekSecondNull) {
            assertNull(P.peekSecondBlock());
        } else {
            assertNotNull(P.peekSecondBlock());
        }
    }
    private void check(RProfiler P, int count, int stackSize) {
        assertEquals(count, P.getNodeCount());
        assertEquals(stackSize, P.getStack().size());
    }

    private void check(Block block, String name, boolean step, boolean root) {
        assertNotNull(block);
        assertEquals(name, block.name);
        assertEquals(step, block.isStep);
        assertEquals(root, block.isRoot);
    }

    private void check(Block block, String name, boolean step, boolean root,
                       long start, long prevStart, long prevEnd, long prevElapsed,
                       long elapsedMin, long elapsedMax, long totalElapsed,
                       int iterations, int chCount, int estIterations, long lastPrintTime) {
        check(block, name, step, root);
        assertEquals(start, block.start);
        assertEquals(prevStart, block.prevStart);
        assertEquals(prevEnd, block.prevEnd);
        assertEquals(prevElapsed, block.prevElapsed);
        assertEquals(elapsedMin, block.minElapsed);
        assertEquals(elapsedMax, block.maxElapsed);
        assertEquals(totalElapsed, block.totalElapsed);
        assertEquals(iterations, block.iterations);
        assertEquals(chCount, block.children.size());
        assertEquals(estIterations, block.estimatedIterations);
        assertEquals(lastPrintTime, block.lastPrintTime);
    }

    private void check(BlockTimingResult result, String name) {
        assertNotNull(result);
        assertEquals(name, result.getName());
    }
}
