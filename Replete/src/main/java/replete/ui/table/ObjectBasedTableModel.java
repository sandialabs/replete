package replete.ui.table;

// Table models can implement this if they have a 1:1 correspondence
// between the rows shown and objects in the model that directly
// correspond to those rows.

public interface ObjectBasedTableModel<T> {
    T getRowObject(int row);
}
