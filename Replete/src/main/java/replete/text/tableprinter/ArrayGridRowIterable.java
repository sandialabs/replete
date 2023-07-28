package replete.text.tableprinter;

import java.util.Iterator;

public class ArrayGridRowIterable implements Iterable<TablePrinterRow> {
    private Object[][] data;
    public ArrayGridRowIterable(Object[][] data) {
        this.data = data;
    }
    @Override
    public Iterator<TablePrinterRow> iterator() {
        return new ArrayGridRowIterator(data);
    }
}
