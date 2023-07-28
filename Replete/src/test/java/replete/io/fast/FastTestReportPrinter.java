package replete.io.fast;

import java.util.ArrayList;
import java.util.List;

import org.junit.Ignore;

@Ignore
public class FastTestReportPrinter {
    private static final String TABLE_VALUE = "Target Object";
    private static final String JAVA_OUT_TIME_MSG = "JOT";
    private static final String FAST_OUT_TIME_MSG = "FOT";
    private static final String OUT_TIME_PCT = "%OT";
    private static final String JAVA_IN_TIME_MSG = "JIT";
    private static final String FAST_IN_TIME_MSG = "FIT";
    private static final String IN_TIME_PCT = "%IT";
    private static final String JAVA_SIZE_MSG = "JSz";
    private static final String FAST_SIZE_MSG = "FSz";
    private static final String SIZE_PCT = "%Sz";
    private static final String JAVA_EQ_MSG = "=";
    private static final String FAST_EQ_MSG = "=";

    public static void printReport(FastTest[] tests) {

        List<Column> columns = new ArrayList<Column>();
        columns.add(new Column(TABLE_VALUE, 's', true));
        columns.add(new Column(JAVA_OUT_TIME_MSG, 'd', false));
        columns.add(new Column(FAST_OUT_TIME_MSG, 'd', false));
        columns.add(new Column(OUT_TIME_PCT, 's', false));
        columns.add(new Column(JAVA_IN_TIME_MSG, 'd', false));
        columns.add(new Column(FAST_IN_TIME_MSG, 'd', false));
        columns.add(new Column(IN_TIME_PCT, 's', false));
        columns.add(new Column(JAVA_SIZE_MSG, 'd', false));
        columns.add(new Column(FAST_SIZE_MSG, 'd', false));
        columns.add(new Column(SIZE_PCT, 's', false));
        columns.add(new Column(JAVA_EQ_MSG, 's', false));
        columns.add(new Column(FAST_EQ_MSG, 's', false));

        List<List<Object>> values = new ArrayList<List<Object>>();
        for(FastTest test : tests) {
            List<Object> rowValues = new ArrayList<Object>();
            rowValues.add(test.toString());
            rowValues.add(test.javaOutTime);
            rowValues.add(test.fastOutTime);
            rowValues.add(test.otPct);
            rowValues.add(test.javaInTime);
            rowValues.add(test.fastInTime);
            rowValues.add(test.itPct);
            rowValues.add(test.javaSize);
            rowValues.add(test.fastSize);
            rowValues.add(test.sizePct);
            rowValues.add(test.javaEquals);
            rowValues.add(test.fastEquals);

            values.add(rowValues);
        }
        ReportPrinter.printReport("Performance & Size Comparison: Java vs. Fast", columns, values);
    }
}
