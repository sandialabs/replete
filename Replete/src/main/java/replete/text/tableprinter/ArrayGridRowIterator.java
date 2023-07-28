package replete.text.tableprinter;

import java.util.Iterator;

public class ArrayGridRowIterator implements Iterator<TablePrinterRow> {
    private int rowIndex = 0;
    private Object[][] data;
    public ArrayGridRowIterator(Object[][] data) {
        this.data = data;
    }
    @Override
    public boolean hasNext() {
        return rowIndex < data.length;
    }
    @Override
    public TablePrinterRow next() {
        Object[] row = data[rowIndex++];
        return c -> row[c];
    }
}
