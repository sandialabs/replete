package replete.io.fast;

import java.util.List;

import org.junit.Ignore;

import replete.text.StringUtil;

@Ignore
public class ReportPrinter {
    public static void printReport(String title, List<Column> columns, List<List<Object>> values) {
        calculateColumnWidths(columns, values);
        int width = calculateWidth(columns);
        printTitle(title, width);
        printDivider(width);
        printRow(columns, null, true);
        printDivider(width);
        for(List<Object> row : values) {
            printRow(columns, row, false);
        }
        printDivider(width);
    }

    private static void calculateColumnWidths(List<Column> columns, List<List<Object>> values) {
        for(int c = 0; c < columns.size(); c++) {
            Column col = columns.get(c);
            for(int r = 0; r < values.size(); r++) {
                Object val = values.get(r).get(c);
                if(val != null && val.toString().length() > col.width) {
                    col.width = val.toString().length();
                }
            }
        }
    }

    private static int calculateWidth(List<Column> columns) {
        int width = 0;
        for(Column col : columns) {
            width += Math.max(col.header.length(), col.width) + 3;
        }
        width++;
        return width;
    }

    private static void printTitle(String title, int width) {
        printDivider(width);
        int headerSpace = width - 4;
        int leftSpaces = (headerSpace - title.length()) / 2;
        String ls = StringUtil.spaces(leftSpaces);
        String rs = StringUtil.spaces(width - 4 - title.length() - ls.length());
        System.out.printf("| " + ls + "%s" + rs + " |%n", title);
    }

    private static void printDivider(int width) {
        for(int i = 0; i < width; i++) {
            System.out.print('-');
        }
        System.out.println();
    }

    private static void printRow(List<Column> columns, List<Object> values, boolean headers) {
        for(int c = 0; c < columns.size(); c++) {
            Column col = columns.get(c);
            String msg = "| %";
            if(col.left || headers) {
                msg += "-";
            }
            msg += Math.max(col.header.length(), col.width);
            msg += headers ? "s" : col.code;
            msg += " ";
            Object val = headers ? col.header : values.get(c);
            System.out.printf(msg, val);
        }
        System.out.printf("|%n");
    }
}
