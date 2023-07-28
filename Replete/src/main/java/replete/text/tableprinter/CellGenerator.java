package replete.text.tableprinter;

public interface CellGenerator<T> {
    Object getCell(T rowData);
}
