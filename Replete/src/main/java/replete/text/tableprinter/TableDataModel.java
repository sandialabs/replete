package replete.text.tableprinter;

public interface TableDataModel {
    int getRowCount();
    Object getValue(int row, int col);
}
