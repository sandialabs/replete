package replete.util;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import replete.collections.RLinkedHashMap;

public class DateUtilTest {

    @Test
    public void elapsedString() {
        boolean EXP_YES = true;
        boolean EXP_NO  = false;
        boolean SP_YES  = true;
        boolean SP_NO   = false;

        RLinkedHashMap<Boolean,
            RLinkedHashMap<Boolean,
                RLinkedHashMap<ElapsedVerbosity,
                    RLinkedHashMap<Long, String>>>> eMap = M()
            .p(EXP_YES, M()
                .p(SP_YES, M()
                    .p(ElapsedVerbosity.SHORT, M()
                        .p(
                               -3500L, "-3 s 500 ms",
                                -500L, "-500 ms",
                                   0L, "0 s",
                                   1L, "1 ms",
                                  10L, "10 ms",
                                1000L, "1 s",
                              100000L, "1 m 40 s",
                             1458796L, "24 m 18 s 796 ms",
                            10000000L, "2 h 46 m 40 s"
                        )
                    )
                    .p(ElapsedVerbosity.MED, M()
                        .p(
                               -3500L, "-3 sec 500 msec",
                                -500L, "-500 msec",
                                   0L, "0 sec",
                                   1L, "1 msec",
                                  10L, "10 msec",
                                1000L, "1 sec",
                              100000L, "1 min 40 sec",
                             1458796L, "24 min 18 sec 796 msec",
                            10000000L, "2 hr 46 min 40 sec"
                        )
                    )
                    .p(ElapsedVerbosity.LONG, M()
                        .p(
                               -3500L, "-3 seconds 500 milliseconds",
                                -500L, "-500 milliseconds",
                                   0L, "0 seconds",
                                   1L, "1 millisecond",
                                  10L, "10 milliseconds",
                                1000L, "1 second",
                              100000L, "1 minute 40 seconds",
                             1458796L, "24 minutes 18 seconds 796 milliseconds",
                            10000000L, "2 hours 46 minutes 40 seconds"
                        )
                    )
                )
                .p(SP_NO, M()
                    .p(ElapsedVerbosity.SHORT, M()
                        .p(
                               -3500L, "-3s 500ms",
                                -500L, "-500ms",
                                   0L, "0s",
                                   1L, "1ms",
                                  10L, "10ms",
                                1000L, "1s",
                              100000L, "1m 40s",
                             1458796L, "24m 18s 796ms",
                            10000000L, "2h 46m 40s"
                        )
                    )
                    .p(ElapsedVerbosity.MED, M()
                        .p(
                               -3500L, "-3sec 500msec",
                                -500L, "-500msec",
                                   0L, "0sec",
                                   1L, "1msec",
                                  10L, "10msec",
                                1000L, "1sec",
                              100000L, "1min 40sec",
                             1458796L, "24min 18sec 796msec",
                            10000000L, "2hr 46min 40sec"
                        )
                    )
                    .p(ElapsedVerbosity.LONG, M()
                        .p(
                               -3500L, "-3 seconds 500 milliseconds",
                                -500L, "-500 milliseconds",
                                   0L, "0 seconds",
                                   1L, "1 millisecond",
                                  10L, "10 milliseconds",
                                1000L, "1 second",
                              100000L, "1 minute 40 seconds",
                             1458796L, "24 minutes 18 seconds 796 milliseconds",
                            10000000L, "2 hours 46 minutes 40 seconds"
                        )
                    )
                )
            )
            .p(EXP_NO, M()
                .p(SP_YES, M()
                    .p(ElapsedVerbosity.SHORT, M()
                        .p(
                               -3500L, "-3 s",
                                -500L, ">-1 s",
                                   0L, "0 s",
                                   1L, "<1 s",
                                  10L, "<1 s",
                                1000L, "1 s",
                              100000L, "1 m 40 s",
                             1458796L, "24 m 18 s",
                            10000000L, "2 h 46 m 40 s"
                        )
                    )
                    .p(ElapsedVerbosity.MED, M()
                        .p(
                               -3500L, "-3 sec",
                                -500L, ">-1 sec",
                                   0L, "0 sec",
                                   1L, "<1 sec",
                                  10L, "<1 sec",
                                1000L, "1 sec",
                              100000L, "1 min 40 sec",
                             1458796L, "24 min 18 sec",
                            10000000L, "2 hr 46 min 40 sec"
                        )
                    )
                    .p(ElapsedVerbosity.LONG, M()
                        .p(
                               -3500L, "-3 seconds",
                                -500L, ">-1 second",
                                   0L, "0 seconds",
                                   1L, "<1 second",
                                  10L, "<1 second",
                                1000L, "1 second",
                              100000L, "1 minute 40 seconds",
                             1458796L, "24 minutes 18 seconds",
                            10000000L, "2 hours 46 minutes 40 seconds"
                        )
                    )
                )
                .p(SP_NO, M()
                    .p(ElapsedVerbosity.SHORT, M()
                        .p(
                               -3500L, "-3s",
                                -500L, ">-1s",
                                   0L, "0s",
                                   1L, "<1s",
                                  10L, "<1s",
                                1000L, "1s",
                              100000L, "1m 40s",
                             1458796L, "24m 18s",
                            10000000L, "2h 46m 40s"
                        )
                    )
                    .p(ElapsedVerbosity.MED, M()
                        .p(
                               -3500L, "-3sec",
                                -500L, ">-1sec",
                                   0L, "0sec",
                                   1L, "<1sec",
                                  10L, "<1sec",
                                1000L, "1sec",
                              100000L, "1min 40sec",
                             1458796L, "24min 18sec",
                            10000000L, "2hr 46min 40sec"
                        )
                    )
                    .p(ElapsedVerbosity.LONG, M()
                        .p(
                               -3500L, "-3 seconds",
                                -500L, ">-1 second",
                                   0L, "0 seconds",
                                   1L, "<1 second",
                                  10L, "<1 second",
                                1000L, "1 second",
                              100000L, "1 minute 40 seconds",
                             1458796L, "24 minutes 18 seconds",
                            10000000L, "2 hours 46 minutes 40 seconds"
                        )
                    )
                )
            )
        ;

        long[] times = {-3500, -500, 0, 1, 10, 1000, 100000, 1458796, 10000000};
        for(boolean expMs : new boolean[] {true, false}) {
            for(boolean spaces : new boolean[] {true, false}) {
                for(ElapsedVerbosity level : ElapsedVerbosity.values()) {
                    for(long time : times) {
                        String actual = DateUtil.toElapsedString(time, level, spaces, expMs);
                        String expected = eMap.get(expMs).get(spaces).get(level).get(time);
                        if(!actual.equals(expected)) {
                            System.out.println("Actual = [" + actual + "]");
                            System.out.println("Expected = [" + expected + "]");
                            System.out.println("ExpandMs = " + expMs);
                            System.out.println("    Spaces = " + spaces);
                            System.out.println("        Level = " + level);
                            System.out.println("            " + time + " = [" + DateUtil.toElapsedString(time, level, spaces, expMs) + "]");
                        }
                        assertEquals(expected, actual);
                    }
                }
            }
        }
    }

    private RLinkedHashMap M() {
        return new RLinkedHashMap<>();
    }
}
