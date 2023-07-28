package replete.text.tableprinter;

import java.util.Iterator;

public class TableDataModelRowIterable implements Iterable<TablePrinterRow> {
    private TableDataModel dataModel;
    public TableDataModelRowIterable(TableDataModel dataModel) {
        this.dataModel = dataModel;
    }
    @Override
    public Iterator<TablePrinterRow> iterator() {
        return new TableDataModelRowIterator(dataModel);
    }
}
