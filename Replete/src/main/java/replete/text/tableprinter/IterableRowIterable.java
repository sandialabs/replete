package replete.text.tableprinter;

import java.util.Iterator;

public class IterableRowIterable<T> implements Iterable<TablePrinterRow> {
    private TableWriter<T> printer;
    private Iterable<T> iterable;
    public IterableRowIterable(TableWriter<T> printer, Iterable<T> iterable) {
        this.printer = printer;
        this.iterable = iterable;
    }
    @Override
    public Iterator<TablePrinterRow> iterator() {
        return new IterableRowIterator<T>(printer, iterable);
    }
}
