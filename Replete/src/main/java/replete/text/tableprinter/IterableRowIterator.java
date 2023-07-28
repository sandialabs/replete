package replete.text.tableprinter;

import java.util.Iterator;

public class IterableRowIterator<T> implements Iterator<TablePrinterRow> {

    private TableWriter<T> printer;
    private Iterator<T> iterator;
    public IterableRowIterator(TableWriter<T> printer, Iterable<T> iterable) {
        this.printer = printer;
        iterator = iterable.iterator();
    }
    @Override
    public boolean hasNext() {
        return iterator.hasNext();
    }
    @Override
    public TablePrinterRow next() {
        T row = iterator.next();
        return c -> {
            ColumnDescriptor<T> col = printer.getColumns().get(c);
            CellGenerator<T> cellGenerator = col.getCellGenerator();
            if(cellGenerator == null) {
                throw new RuntimeException("No cell generator defined for column '" + col.getName() + "'");
            }
            return cellGenerator.getCell(row);
        };
    }
}
