package replete.text.tableprinter;

import java.util.ArrayList;
import java.util.List;

import replete.text.RStringBuilder;
import replete.text.StringUtil;

public class TableWriter<T> {


    ////////////
    // FIELDS //
    ////////////

    private static final String DEFAULT_NEWLINE = System.lineSeparator();
    public static final int DEFAULT_COLUMN_SEPARATION = 2;

    private List<ColumnDescriptor<T>> columns = new ArrayList<>();
    private int columnSeparation = DEFAULT_COLUMN_SEPARATION;
    private boolean printHeaders = true; // currently only for CSVs
    private boolean printHeaderDashes = true;
    private boolean printCountLabel = false;
    private String countLabelEntityName = null;
    private boolean printBottomDashes = false;
    private boolean printBottomHeaders = false;
    private String newLine = DEFAULT_NEWLINE;


    //////////////////////////
    // ACCESSORS / MUTATORS //
    //////////////////////////

    // Accessors

    public int getColumnSeparation() {
        return columnSeparation;
    }
    public boolean isPrintHeaders() {
        return printHeaders;
    }
    public boolean isPrintHeaderDashes() {
        return printHeaderDashes;
    }
    public List<ColumnDescriptor<T>> getColumns() {
        return columns;
    }
    public boolean isPrintBottomDashes() {
        return printBottomDashes;
    }
    public String getNewLine() {
        return newLine;
    }

    // Accessors (Computed)

    public int getColumnsSize() {
        return columns.size();
    }

    // Mutators

    public TableWriter<T> setColumnSeparation(int columnSeparation) {
        this.columnSeparation = columnSeparation;
        return this;
    }
    public TableWriter<T> setPrintHeaders(boolean printHeaders) {
        this.printHeaders = printHeaders;
        return this;
    }
    public TableWriter<T> setPrintHeaderDashes(boolean printHeaderDashes) {
        this.printHeaderDashes = printHeaderDashes;
        return this;
    }
    public TableWriter<T> setPrintBottomDashes(boolean printBottomDashes) {
        this.printBottomDashes = printBottomDashes;
        return this;
    }
    public TableWriter<T> setPrintBottomHeaders(boolean printBottomHeaders) {
        this.printBottomHeaders = printBottomHeaders;
        return this;
    }
    public TableWriter<T> setNewLine(String newLine) {
        this.newLine = newLine;
        return this;
    }

    public TableWriter<T> addColumn(String name) {
        return addColumn(name, TableColumnAlignment.LEFT, null);
    }
    public TableWriter<T> addColumn(String name, TableColumnAlignment alignment) {
        return addColumn(name, alignment, null);
    }
    public TableWriter<T> addColumn(String name, CellGenerator<T> cellGenerator) {
        return addColumn(name, TableColumnAlignment.LEFT, cellGenerator);
    }
    public TableWriter<T> addColumn(String name, TableColumnAlignment alignment, CellGenerator<T> cellGenerator) {
        ColumnDescriptor<T> c = new ColumnDescriptor<>(name, alignment, cellGenerator);
        columns.add(c);
        return this;
    }
    public TableWriter<T> addColumn(String name, CellGenerator<T> cellGenerator, TableColumnAlignment alignment) {
        ColumnDescriptor<T> c = new ColumnDescriptor<>(name, alignment, cellGenerator);
        columns.add(c);
        return this;            // ^^ Two parameter order options for formatting purposes ;)
    }

    public TableWriter<T> setPrintCountLabel(boolean printCountLabel) {
        this.printCountLabel = printCountLabel;
        return this;
    }
    public TableWriter<T> setCountLabelEntityName(String countLabelEntityName) {
        this.countLabelEntityName = countLabelEntityName;
        return this;
    }


    ////////////
    // RENDER //
    ////////////

    public String render(Object[][] data) {
        return renderInner(new ArrayGridRowIterable(data));
    }
    public String render(TableDataModel dataSource) {
        return renderInner(new TableDataModelRowIterable(dataSource));
    }
    public String render(Iterable<T> iterable) {
        return renderInner(new IterableRowIterable<T>(this, iterable));
    }

    public String renderToCsv(Object[][] data) {
        return renderInnerToCsv(new ArrayGridRowIterable(data));
    }
    public String renderToCsv(TableDataModel dataSource) {
        return renderInnerToCsv(new TableDataModelRowIterable(dataSource));
    }
    public String renderToCsv(Iterable<T> iterable) {
        return renderInnerToCsv(new IterableRowIterable<T>(this, iterable));
    }


    ////////////////
    // RENDER CSV //
    ////////////////

    private String renderInnerToCsv(Iterable<TablePrinterRow> iterable) {
        RStringBuilder buffer = new RStringBuilder().setNewLine(newLine);
        if(printHeaders) {
            appendColumHeadersToCsv(buffer);
        }
        appendDataToCsv(buffer, iterable);
        return buffer.toString();
    }

    private void appendColumHeadersToCsv(RStringBuilder buffer) {
        for(int c = 0; c < columns.size(); c++) {
            ColumnDescriptor<T> column = columns.get(c);
            boolean quote = column.getName().contains(",");
            if(quote) {
                buffer.append('"');
            }
            buffer.append(column.getName());
            if(quote) {
                buffer.append('"');
            }
            if(c != columns.size() - 1) {
                buffer.append(',');
            }
        }
        buffer.appendln();
    }

    private void appendDataToCsv(RStringBuilder buffer, Iterable<TablePrinterRow> iterable) {
        for(TablePrinterRow row : iterable) {
            for(int c = 0; c < columns.size(); c++) {
                ColumnDescriptor<T> column = columns.get(c);
                Object cell = row.getData(c);
                String image = renderCell(cell, c);
                boolean quote = image.contains(",");
                if(quote) {
                    buffer.append('"');
                }
                buffer.append(image);    // TODO: what to do if image contains "
                if(quote) {
                    buffer.append('"');
                }
                if(c != columns.size() - 1) {
                    buffer.append(',');
                }
            }
            buffer.appendln();
        }
    }


    //////////////////
    // RENDER TABLE //
    //////////////////

    private String renderInner(Iterable<TablePrinterRow> iterable) {
        RStringBuilder buffer = new RStringBuilder().setNewLine(newLine);
        int[] widths = determineMaximumDataWidths(iterable);
        String sp = StringUtil.spaces(columnSeparation);
        appendColumHeaders(buffer, widths, sp);
        if(printHeaderDashes) {
            appendColumHeaderDashes(buffer, widths, sp);
        }
        int rowCount = appendData(buffer, iterable, widths, sp);
        if(printBottomHeaders) {
            if(printHeaderDashes) {
                appendColumHeaderDashes(buffer, widths, sp);
            }
            appendColumHeaders(buffer, widths, sp);
        }
        if(printCountLabel) {
            String label = countLabelEntityName == null ? "rows" : countLabelEntityName;
            buffer.appendln("(" + rowCount + " " + label + ")");
        }
        return buffer.toString();
    }

    private String renderCell(Object cell, int c) {
        //ColumnDescriptor<T> column = columns.get(c);
        return StringUtil.cleanNull(cell);
    }

    private int[] determineMaximumDataWidths(Iterable<TablePrinterRow> iterable) {
        int[] widths = new int[columns.size()];

        for(int c = 0; c < widths.length; c++) {
            for(TablePrinterRow row : iterable) {
                Object cell = row.getData(c);
                String image = renderCell(cell, c);
                int len = image.length();
                if(len > widths[c]) {
                    widths[c] = len;
                }
            }
            int len = columns.get(c).getName().length();
            if(len > widths[c]) {
                widths[c] = len;
            }
        }

        return widths;
    }

    private void appendColumHeaders(RStringBuilder buffer, int[] widths, String sp) {
        for(int c = 0; c < widths.length; c++) {
            ColumnDescriptor<T> column = columns.get(c);
            String m = "-";   //column.alignment == TableColumnAlignment.LEFT ? "-" : "";

            if(c == columns.size() - 1 && m.equals("-")) {
                buffer.append(column.getName());  // Special case we don't potentially print a million extra spaces in the last column
            } else {
                buffer.appendf("%" + m + widths[c] + "s", column.getName());
            }

            if(c != columns.size() - 1) {
                buffer.append(sp);
            }
        }
        buffer.appendln();
    }

    private void appendColumHeaderDashes(RStringBuilder buffer, int[] widths, String sp) {
        for(int c = 0; c < columns.size(); c++) {
            ColumnDescriptor<T> column = columns.get(c);
            String m = column.getAlignment() == null || column.getAlignment() == TableColumnAlignment.LEFT ? "-" : "";

            if(c == columns.size() - 1 && m.equals("-")) {
                int len = column.getName().length();
                String dashes = StringUtil.replicateChar('-', len);
                buffer.append(dashes);   // Special case we don't potentially print a million extra hyphens in the last column
            } else {
                String dashes = StringUtil.replicateChar('-', widths[c]);
                buffer.appendf("%" + m + widths[c] + "s", dashes);
            }

            if(c != columns.size() - 1) {
                buffer.append(sp);
            }
        }
        buffer.appendln();
    }
    private int appendData(RStringBuilder buffer, Iterable<TablePrinterRow> iterable, int[] widths, String sp) {
        int rowCount = 0;
        int totalLength = 0;
        for(TablePrinterRow row : iterable) {
            int totalLengthCol = 0;
            for(int c = 0; c < columns.size(); c++) {
                ColumnDescriptor<T> column = columns.get(c);
                Object cell = row.getData(c);
                String image = renderCell(cell, c);
                String m = column.getAlignment() == null || column.getAlignment() == TableColumnAlignment.LEFT ? "-" : "";

                if(c == columns.size() - 1 && m.equals("-")) {
                    buffer.append(image);  // Special case we don't potentially print a million extra spaces in the last column
                } else {
                    buffer.appendf("%" + m + widths[c] + "s", image);
                }
                totalLengthCol += widths[c];

                if(c != columns.size() - 1) {
                    buffer.append(sp);
                    totalLengthCol += sp.length();
                }
            }
            buffer.appendln();
            rowCount++;
            totalLength = totalLengthCol;
        }
        if(printBottomDashes) {
            buffer.appendln(StringUtil.replicateChar('-', totalLength));
        }
        return rowCount;
    }
}
