package replete.gc;

import java.lang.management.GarbageCollectorMXBean;
import java.lang.management.MemoryUsage;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.sun.management.GarbageCollectionNotificationInfo;

import replete.collections.ArrayUtil;
import replete.compare.GroupSequenceComparator;
import replete.io.FileUtil;
import replete.numbers.NumUtil;
import replete.text.RStringBuilder;
import replete.text.StringUtil;
import replete.util.DateUtil;


public class StandardConsoleGcNotificationListener implements GcNotificationListener {


    ////////////
    // FIELDS //
    ////////////

    // Count of the total time spent in each GC
    private Map<GarbageCollectorMXBean, Long> totalGcDurations = new HashMap<>();


    ////////////////
    // OVERRIDDEN //
    ////////////////

    @Override
    public void handle(GarbageCollectorMXBean gcBean, GarbageCollectionNotificationInfo info) {
        RStringBuilder buffer = new RStringBuilder();
        buffer.appendln("|--------> ! <--------|");
        buffer.appendln("** Garbage Collector **");
        buffer.append(" - Now:            "); buffer.appendln(DateUtil.toLongString(System.currentTimeMillis()));
        buffer.append(" - ID:             "); buffer.appendln(info.getGcInfo().getId());
        buffer.append(" - Name:           "); buffer.appendln(info.getGcName());
        buffer.append(" - Cum Coll Count: "); buffer.appendln(gcBean.getCollectionCount());         // Should always be the same as ID
        buffer.append(" - Cum Coll Dur:   "); buffer.appendln(gcBean.getCollectionTime() + " ms");  // Slightly more granular cumulative duration than "Duration" summed together
        buffer.append(" - Action:         "); buffer.appendln(info.getGcAction() + " (" + getActionLabel(info.getGcAction()) + ")");
        buffer.append(" - Cause:          "); buffer.appendln(info.getGcCause());
        buffer.append(" - Start:          "); buffer.appendln(info.getGcInfo().getStartTime() + " ms (since JVM start)");
        buffer.append(" - End:            "); buffer.appendln(info.getGcInfo().getEndTime() + " ms (since JVM start)");
        buffer.append(" - Duration:       "); buffer.appendln(info.getGcInfo().getDuration() + " ms");
        String coPct = calculateCumulativeOverhead(gcBean, info);
        buffer.append(" - Cum Overhead:   "); buffer.appendln(coPct);
        buffer.append(" - Pools Managed:  "); buffer.appendln(ArrayUtil.toString(gcBean.getMemoryPoolNames()));
        buffer.appendln(" - Memory:");

        // Get the information about each memory space, and pretty print it
        Map<String, MemoryUsage> memBefore = info.getGcInfo().getMemoryUsageBeforeGc();
        Map<String, MemoryUsage> memAfter  = info.getGcInfo().getMemoryUsageAfterGc();

        List<String> sortedPoolNames = new ArrayList<>(memAfter.keySet());
        Collections.sort(sortedPoolNames, new GroupSequenceComparator<>(new Object[] {
            "PS Eden Space", "PS Survivor Space", "PS Old Gen", GroupSequenceComparator.OTHERS
        }));

        //appendMemoryPoolInfo(buffer, memBefore, null, sortedPoolNames);    // To show the full before snapshot
        appendMemoryPoolInfo(buffer, memAfter, memBefore, sortedPoolNames);

        buffer.appendln("    (used <= committed <= max)");

        buffer.appendln("|--------> ! <--------|");
        System.out.print(buffer);
    }

    private void appendMemoryPoolInfo(RStringBuilder buffer,
                                      Map<String, MemoryUsage> pools,
                                      Map<String, MemoryUsage> poolsBefore,  // Optional
                                      List<String> sortedPoolNames) {
        String[][] rows = new String[pools.size() + 1][8];
        rows[0][0] = "Pool Name";
        rows[0][1] = "Expandable";
        rows[0][2] = "Initial";
        rows[0][3] = "Used";
        rows[0][4] = "Committed";
        rows[0][5] = "Max";
        rows[0][6] = "Usage %";
        rows[0][7] = "Usage % Delta";
        int r = 1;

        for(String name : sortedPoolNames) {
            MemoryUsage details = pools.get(name);

            long memInit = details.getInit();
            long memUsed = details.getUsed();
            long memCmtd = details.getCommitted();
            long memMax  = details.getMax();

            String pctBefore = "";
            if(poolsBefore != null) {
                MemoryUsage detailsBefore = poolsBefore.get(name);
                long memUsedBefore = detailsBefore.getUsed();
                long memCmtdBefore = detailsBefore.getCommitted();
                pctBefore = NumUtil.pct(memUsedBefore, memCmtdBefore);
            }
            String pct = NumUtil.pct(memUsed, memCmtd);

            rows[r][0] = name;
            rows[r][1] = StringUtil.yesNo(memCmtd != memMax);
            rows[r][2] = memInit == -1 ? "Undef" : FileUtil.getReadableSizeString(memInit);
            rows[r][3] = FileUtil.getReadableSizeString(memUsed);
            rows[r][4] = FileUtil.getReadableSizeString(memCmtd);
            rows[r][5] = memMax == -1 ? "Undef" : FileUtil.getReadableSizeString(memMax);
            rows[r][6] = pct;
            rows[r][7] = poolsBefore != null ? pctBefore + " -> " + pct : "";
            r++;
        }

        // Create memory data table format string
        int[] ml = StringUtil.maxLengths(rows);
        String fmt = "";
        for(int c = 0; c < ml.length; c++) {
            String align = (c <= 1) ? "-" : "";
            fmt += "%" + align + ml[c] + "s  ";
        }
        fmt = "    " + fmt.trim() + "%n";

        // Print the memory data table
        r = 0;
        for(String[] row : rows) {
            buffer.appendf(fmt, (Object[]) row);
            if(r == 0) {
                String[] dashes = new String[row.length];
                for(int c = 0; c < row.length; c++) {
                    dashes[c] = StringUtil.replicateChar('=', ml[c]);
                }
                buffer.appendf(fmt, (Object[]) dashes);
            }
            r++;
        }
    }

    private String getActionLabel(String action) {
        if(action.equals("end of minor GC")) {
            return "Young Gen GC";
        } else if(action.equals("end of major GC")) {
            return "Old Gen GC";
        }
        return "Unknown";
    }

    private String calculateCumulativeOverhead(GarbageCollectorMXBean gcBean,
                                               GarbageCollectionNotificationInfo info) {
        Long dur = totalGcDurations.get(gcBean);
        if(dur == null) {
            dur = 0L;
        }
        dur += info.getGcInfo().getDuration();
        totalGcDurations.put(gcBean, dur);
        return NumUtil.pct(dur, info.getGcInfo().getEndTime());
    }
}
