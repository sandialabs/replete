package replete.text.tableprinter;

import java.util.Iterator;

public class TableDataModelRowIterator implements Iterator<TablePrinterRow> {
    private int rowIndex = 0;
    private TableDataModel dataSource;
    public TableDataModelRowIterator(TableDataModel dataSource) {
        this.dataSource = dataSource;
    }
    @Override
    public boolean hasNext() {
        return rowIndex < dataSource.getRowCount();
    }
    @Override
    public TablePrinterRow next() {
        int r = rowIndex++;
        return c -> dataSource.getValue(r, c);
    }
}
